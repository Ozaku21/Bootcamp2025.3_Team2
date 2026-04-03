import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";



const responseTimeTrend = new Trend('forward_rates_duration');

export const options = {
  stages: [
    { duration: '30s', target: 25 },
    { duration: '1m', target: 25 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
    forward_rates_duration: ['p(95)<500'],
  },
};

export function handleSummary(data) {
  return {
    "performance-results/ForwardRatesLoadReport.html": htmlReport(data),
  };
}

export default function () {
  const url = 'https://apigw.tbcbank.ge/api/v1/forwardRates/getForwardRates?locale=ka-GE';

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    timeout: '2s',
  };

  const res = http.get(url, params);

  console.log('Status: ${res.status} | Duration: ${res.timings.duration.toFixed(2)}ms');

  responseTimeTrend.add(res.timings.duration);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  if (res.status === 200) {
    const body = res.json();

    check(body, {
      'has rates': (b) => b.hasOwnProperty('rates'),
      'rates not empty': (b) => b.rates.length > 0,
      'updateDate exists': (b) => b.hasOwnProperty('updateDate'),
    });

  } else {
    console.warn('Request blocked or failed. Status: ${res.status}');
  }

  sleep(1);
}