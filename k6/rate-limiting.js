import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

// Demonstrates the API Gateway's Redis token bucket rate limiter
// (replenishRate: 10/s, burstCapacity: 20 — see api-gateway/src/main/resources/application.yaml).
// The limiter is a single global bucket per key (default-filters applies it to every route,
// keyed by IP here since we're unauthenticated), so hitting multiple public routes still shares
// one budget — this just spreads the demo traffic across routeId=user and routeId=auth so the
// Grafana dashboard has more than one route to show.

const BASE_URL = __ENV.BASE_URL || 'https://localhost:8085';

export const options = {
  insecureSkipTLSVerify: true, // gateway uses a self-signed cert
  scenarios: {
    rate_limit_spike: {
      executor: 'ramping-arrival-rate',
      startRate: 5,
      timeUnit: '1s',
      preAllocatedVUs: 50,
      maxVUs: 100,
      stages: [
        { target: 5, duration: '10s' },   // below replenishRate — should all pass
        { target: 30, duration: '5s' },   // spike past burstCapacity — 429s expected
        { target: 30, duration: '20s' },  // hold above replenishRate — sustained 429s
        { target: 0, duration: '10s' },   // recovery
      ],
    },
  },
};

export const allowed = new Counter('rate_limit_allowed');
export const throttled = new Counter('rate_limit_throttled');

const requests = [
  // routeId=user — public read endpoint
  () => http.get(`${BASE_URL}/api/v1/users`),
  // routeId=auth — public token issuance endpoint, no valid user needed
  () =>
    http.post(
      `${BASE_URL}/api/v1/tokens/issue`,
      JSON.stringify({ userId: '00000000-0000-0000-0000-000000000000', username: 'k6-load-test' }),
      { headers: { 'Content-Type': 'application/json' } },
    ),
];

export default function () {
  const request = requests[Math.floor(Math.random() * requests.length)];
  const res = request();

  check(res, {
    'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
  });

  if (res.status === 429) {
    throttled.add(1);
  } else if (res.status === 200) {
    allowed.add(1);
  }

  sleep(0.1);
}
