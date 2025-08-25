import http from 'k6/http';
import { check, sleep } from 'k6';

// ---- 튜닝 파라미터 (환경변수로 바꿔치기 가능) ----
const BASE           = __ENV.TARGET || 'http://host.docker.internal:8080';
const START_RPS      = Number(__ENV.START || 0);     // 시작 RPS
const END_RPS        = Number(__ENV.END   || 200);   // 목표 RPS
const STEP_RPS       = Number(__ENV.STEP  || 10);     // 스텝 크기 (1씩 증가)
const STEP_DURATION  = __ENV.STEP_DURATION || '15s'; // 각 단계 유지 시간
const HOLD_DURATION  = __ENV.HOLD || '30s';          // 최종 RPS 유지 시간
const RAMP_DOWN      = __ENV.RAMP_DOWN || '15s';     // 종료시 다운 시간

// 필요한 VU 수 대략치: RPS * (평균응답시간초 + 생각시간)
// 모르면 보수적으로 END_RPS * 2 ~ 4로 두고 시작
const PRE_VUS = Number(__ENV.PRE_VUS || Math.max(10, END_RPS * 2));
const MAX_VUS = Number(__ENV.MAX_VUS || Math.max(20, END_RPS * 4));

// ---- stages 생성: 0->1->2 ... -> END_RPS ----
const stages = [];
for (let r = START_RPS; r <= END_RPS; r += STEP_RPS) {
    stages.push({ target: r, duration: STEP_DURATION });
}
if (HOLD_DURATION !== '0s') {
    stages.push({ target: END_RPS, duration: HOLD_DURATION }); // plateau
}
stages.push({ target: 0, duration: RAMP_DOWN }); // ramp-down

export const options = {
    discardResponseBodies: true,
    scenarios: {
        ramp_rps: {
            executor: 'ramping-arrival-rate',
            timeUnit: '1s',
            preAllocatedVUs: PRE_VUS,
            maxVUs: MAX_VUS,
            stages,
        },
    },
    thresholds: {
        http_req_failed:   ['rate<0.01'],     // 에러 1% 미만
        http_req_duration: ['p(95)<3000'],    // p95 3s 예시(초반엔 느슨하게)
    },
};

export default function () {
    const sortType = 'LIKES_DESC';
    const size = 20;
    const page = 0;
    const brandId = 1;

    const url = `${BASE}/api/v1/products?brandId=${brandId}&sortType=${sortType}&size=${size}&page=${page}`;

    const res = http.get(url);
    check(res, { 'status 200': (r) => r.status === 200 });

    // 생각시간(optional). arrival-rate 실행기는 RPS를 타이머로 맞추니 sleep은 0이 기본.
    if (__ENV.THINK) sleep(Number(__ENV.THINK));
}
