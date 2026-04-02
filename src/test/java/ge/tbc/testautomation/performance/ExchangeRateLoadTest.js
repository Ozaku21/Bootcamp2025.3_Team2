import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const exchangeRateTrend = new Trend('get_exchange_rate_duration');
const currencyOne = "USD";
const currencySecond = "GEL"

export const options = {
  stages: [
    { duration: '30s', target: 25 },
    { duration: '1m', target: 25 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],    // Error rate must be less than 1%
    http_req_duration: ['p(95)<500'], // 95% of requests must be under 500ms
    get_exchange_rate_duration: ['p(95)<400', 'max<1000'],
  },
};

export function handleSummary(data) {
  return {
    "ExchangeRateLoadReport.html": htmlReport(data),
  };
}

export default function () {
  const url = 'https://apigw.tbcbank.ge/api/v1/exchangeRates/getExchangeRate?Iso1=' + currencyOne + '&Iso2=' + currencySecond;

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    timeout: '3s',
  };

  const res = http.get(url, params);

  console.log(`Status: ${res.status} | Duration: ${res.timings.duration.toFixed(2)}ms`);

  exchangeRateTrend.add(res.timings.duration);

  const statusOk = check(res, {
    'is status 200': (r) => r.status === 200,
  });

  if (statusOk && Math.random() < 0.10) {
    const body = res.json();

    check(body, {
      'correct iso pair': (b) => b.iso1 === 'USD' && b.iso2 === 'GEL',
      'rates are numbers': (b) => typeof b.buyRate === 'number' && typeof b.sellRate === 'number',
      'sellRate >= buyRate': (b) => b.sellRate >= b.buyRate,
      'has positive weight': (b) => b.currencyWeight >= 1,
      'has updateDate': (b) => b.hasOwnProperty('updateDate'),
    });
  } else if (!statusOk) {
    console.warn(`Request failed. Status: ${res.status} | Body: ${res.body}`);
  }

  sleep(1);
}