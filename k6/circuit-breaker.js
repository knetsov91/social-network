import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// Demonstrates the post-service -> user-service circuit breaker (Resilience4j via
// spring-cloud-openfeign, see post-service/src/main/resources/application.yaml:
// slidingWindowSize=10, failureRateThreshold=50%, waitDurationInOpenState=10s).
//
// GET /api/v1/posts/feed calls UserClient.getFollowings() over Feign. When user-service
// is unreachable, calls fail fast and the circuit opens; UserClientFallback then returns
// an empty list immediately, so PostService.getFeed() returns an empty page instead of
// erroring. This script can't stop user-service itself — run it while externally killing
// and restarting user-service partway through (see k6/README or just kill/restart the
// service by hand) to see the state transitions.
//
// Distinguishing real vs fallback responses: fallback responses come back with an empty
// content[] and are near-instant; real responses are populated and take slightly longer
// (an actual Feign call to user-service).

const BASE_URL = __ENV.BASE_URL || 'https://localhost:8085';

export const options = {
  insecureSkipTLSVerify: true,
  scenarios: {
    circuit_breaker_watch: {
      executor: 'constant-vus',
      vus: 2,
      duration: '60s',
    },
  },
};

export const feedPopulated = new Counter('feed_populated');
export const feedEmpty = new Counter('feed_empty');
export const feedError = new Counter('feed_error');
export const populatedLatency = new Trend('feed_populated_duration');
export const emptyLatency = new Trend('feed_empty_duration');

function register(username) {
  const res = http.post(
    `${BASE_URL}/api/v1/users/register`,
    JSON.stringify({ username, password: 'password123', confirmPassword: 'password123' }),
    { headers: { 'Content-Type': 'application/json' } },
  );
  if (res.status !== 201) {
    throw new Error(`register(${username}) failed: ${res.status} ${res.body}`);
  }
}

function login(username) {
  const res = http.post(
    `${BASE_URL}/api/v1/users/login`,
    JSON.stringify({ username, password: 'password123' }),
    { headers: { 'Content-Type': 'application/json' } },
  );
  if (res.status !== 200) {
    throw new Error(`login(${username}) failed: ${res.status} ${res.body}`);
  }
  return {
    id: res.json('id'),
    cookie: `token=${res.cookies.token[0].value}`,
  };
}

export function setup() {
  const ts = Date.now();
  const authorUsername = `author_${ts}`;
  const viewerUsername = `viewer_${ts}`;

  register(authorUsername);
  register(viewerUsername);

  const author = login(authorUsername);
  const viewer = login(viewerUsername);

  const postRes = http.post(
    `${BASE_URL}/api/v1/posts`,
    JSON.stringify({ title: 'Circuit breaker demo', content: 'k6 test post', authorId: author.id }),
    { headers: { 'Content-Type': 'application/json', Cookie: author.cookie } },
  );
  if (postRes.status !== 201) {
    throw new Error(`create post failed: ${postRes.status} ${postRes.body}`);
  }

  const followRes = http.post(
    `${BASE_URL}/api/v1/users/follow`,
    JSON.stringify({ followeId: author.id }),
    { headers: { 'Content-Type': 'application/json', Cookie: viewer.cookie } },
  );
  if (followRes.status !== 200) {
    throw new Error(`follow failed: ${followRes.status} ${followRes.body}`);
  }

  return { viewerCookie: viewer.cookie };
}

export default function (data) {
  const res = http.get(`${BASE_URL}/api/v1/posts/feed`, {
    headers: { Cookie: data.viewerCookie },
  });

  check(res, { 'status is 200': (r) => r.status === 200 });

  if (res.status === 200) {
    const content = res.json('content');
    if (content.length > 0) {
      feedPopulated.add(1);
      populatedLatency.add(res.timings.duration);
    } else {
      feedEmpty.add(1);
      emptyLatency.add(res.timings.duration);
    }
  } else {
    feedError.add(1);
  }

  sleep(1);
}
