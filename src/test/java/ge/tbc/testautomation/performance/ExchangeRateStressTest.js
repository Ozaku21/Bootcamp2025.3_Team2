import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Counter, Rate } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";


const exchangeRateTrend  = new Trend('get_exchange_rate_duration');
const firewallBlockRate   = new Rate('firewall_block_rate');
const firewallBlockCount  = new Counter('firewall_blocks_total');
const retryCount          = new Counter('retries_total');

const BASE_URL      = __ENV.BASE_URL      || 'https://apigw.tbcbank.ge';
const CURRENCY_ONE  = __ENV.CURRENCY_ONE  || 'USD';
const CURRENCY_TWO  = __ENV.CURRENCY_TWO  || 'GEL';
const MAX_RETRIES   = parseInt(__ENV.MAX_RETRIES || '2');

const FIREWALL_STATUSES  = new Set([403, 429, 503]);
const FIREWALL_BODIES    = ['access denied', 'blocked', 'rate limit', 'too many requests', 'waf', 'firewall', 'ddos'];

export const options = {
  stages: [
    { duration: '30s', target: 25  },
    { duration: '1m',  target: 25  },
    { duration: '1m',  target: 75  },
    { duration: '2m',  target: 75  },
    { duration: '15s', target: 150 },
    { duration: '30s', target: 150 },
    { duration: '30s', target: 25  },
    { duration: '1m',  target: 25  },
    { duration: '15s', target: 0   },
  ],

  thresholds: {
    http_req_failed:              ['rate<0.20'],
    http_req_duration:            ['p(95)<2000'],
    get_exchange_rate_duration:   ['p(99)<3000', 'max<5000'],

    firewall_block_rate:          [{ threshold: 'rate<0.50', abortOnFail: false }],
  },
};

export function handleSummary(data) {
  return {
    'ExchangeRateStressReport.html': htmlReport(data),
    stdout: textSummary(data),
  };
}

function isFirewallBlock(res) {
  if (FIREWALL_STATUSES.has(res.status)) return true;

  const bodyLower = (res.body || '').toLowerCase();
  return FIREWALL_BODIES.some(kw => bodyLower.includes(kw));
}


function getWithRetry(url, params) {
  let res;
  for (let attempt = 0; attempt <= MAX_RETRIES; attempt++) {
    res = http.get(url, params);

    if (isFirewallBlock(res)) break;

    const shouldRetry = res.status === 0 || (res.status >= 500 && res.status < 600);
    if (!shouldRetry || attempt === MAX_RETRIES) break;

    retryCount.add(1);
    sleep(0.5 * (attempt + 1));
  }
  return res;
}

export default function () {
  const url = `${BASE_URL}/api/v1/exchangeRates/getExchangeRate?Iso1=${CURRENCY_ONE}&Iso2=${CURRENCY_TWO}`;

  const reqParams = {
    headers: {
      'Content-Type': 'application/json',
      'Accept':        'application/json',
    },
    timeout: '5s',
    tags:    { endpoint: 'getExchangeRate' },
  };

  group('getExchangeRate', () => {
    const res = getWithRetry(url, reqParams);

    exchangeRateTrend.add(res.timings.duration, { endpoint: 'getExchangeRate' });

    const blocked = isFirewallBlock(res);
    firewallBlockRate.add(blocked ? 1 : 0);
    if (blocked) {
      firewallBlockCount.add(1);
      console.warn(`[WAF/Firewall] Status: ${res.status} | VU: ${__VU} | Iter: ${__ITER} | Body snippet: ${(res.body || '').slice(0, 120)}`);

      sleep(Math.random() * 3 + 2);
      return;
    }

    const statusOk = check(res, {
      'is status 200': (r) => r.status === 200,
    });

    if (!statusOk) {
      console.error(`Request failed. Status: ${res.status} | Body: ${(res.body || '').slice(0, 200)}`);
    }

    if (statusOk && Math.random() < 0.10) {
      let body;
      try {
        body = res.json();
      } catch (e) {
        console.error(`JSON parse failed. Status: ${res.status} | Body: ${(res.body || '').slice(0, 200)}`);
        return;
      }

      check(body, {
        'correct iso pair':          (b) => b.iso1 === CURRENCY_ONE && b.iso2 === CURRENCY_TWO,
        'rates are numbers':         (b) => typeof b.buyRate === 'number' && typeof b.sellRate === 'number',
        'sellRate >= buyRate':       (b) => b.sellRate >= b.buyRate,
        'has positive weight':       (b) => b.currencyWeight >= 1,
        'updateDate is valid string':(b) => typeof b.updateDate === 'string' && b.updateDate.length > 0,
      });
    }
  });

  sleep(Math.random() * 1 + 0.2);
}

function textSummary(data) {
  const blocked  = data.metrics.firewall_blocks_total?.values?.count ?? 0;
  const total    = data.metrics.http_reqs?.values?.count ?? 0;
  const blockPct = total > 0 ? ((blocked / total) * 100).toFixed(1) : '0.0';
  const p95      = data.metrics.http_req_duration?.values?.['p(95)']?.toFixed(0) ?? 'N/A';
  const errRate  = ((data.metrics.http_req_failed?.values?.rate ?? 0) * 100).toFixed(2);

  return `
=== Stress Test Summary ===
Total requests   : ${total}
Firewall blocks  : ${blocked} (${blockPct}%)
Error rate       : ${errRate}%
p(95) duration   : ${p95} ms
===========================
`;
}