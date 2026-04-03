import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const responseTimeTrend = new Trend('commercial_list_duration');

export const options = {
  stages: [
    { duration: '0.5m', target: 25 },
    { duration: '1m', target: 25 },
    { duration: '0.5m', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

export function handleSummary(data) {
  return {
    "performance-results/CommercialListLoadReport.html": htmlReport(data),
  };
}

export default function () {
  const url = 'https://apigw.tbcbank.ge/api/v1/exchangeRates/commercialList?locale=ka-GE';

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  };

  const res = http.get(url, params);

  console.log('Status: ${res.status} | Duration: ${res.timings.duration.toFixed(2)}ms');

  responseTimeTrend.add(res.timings.duration);

  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  if (res.status === 200) {
    check(res, {
    'body contains rates': (r) => r.json().hasOwnProperty('rates'),
    'rates array is not empty': (r) => r.json().rates.length > 0,
    'updateDateTime is present': (r) => r.json().hasOwnProperty('updateDateTime'),
  });
  } else {
    console.warn('Request blocked or failed. Status: ${res.status}');
  }

  sleep(1);
}