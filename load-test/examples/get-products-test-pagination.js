import http from 'k6/http';
import { check } from 'k6';
import exec from 'k6/execution';

// ---------- 환경 변수 ----------
const BASE          = __ENV.TARGET || 'http://host.docker.internal:8080';
const SIZE          = Number(__ENV.SIZE || 20);
const SORT          = __ENV.SORT || 'LIKES_DESC';
const BRAND_ID      = __ENV.BRAND_ID || ''; // 없으면 전체
const START_PAGE    = Number(__ENV.START_PAGE || 325);
const END_PAGE      = Number(__ENV.END_PAGE || 500);  // 어디까지 올릴지
const STEP_DURATION = __ENV.STEP_DURATION || '15s';   // 각 페이지마다 안정화 시간
const RPS           = Number(__ENV.RPS || 1);

// STEP_DURATION(문자열) → 초
function parseDurationSec(s) {
    const m = s.match(/^(\d+)(ms|s|m)$/);
    if (!m) throw new Error('STEP_DURATION must be like 15s/2m/500ms');
    const v = Number(m[1]); const u = m[2];
    if (u === 'ms') return v / 1000;
    if (u === 's')  return v;
    if (u === 'm')  return v * 60;
}

const stepSec = parseDurationSec(STEP_DURATION);
const totalPages = END_PAGE - START_PAGE + 1;
const totalDurationSec = totalPages * stepSec;

// ---------- k6 옵션 ----------
export const options = {
    discardResponseBodies: true,
    scenarios: {
        fixed_rps_pages: {
            executor: 'constant-arrival-rate',
            rate: RPS,
            timeUnit: '1s',
            duration: `${Math.ceil(totalDurationSec)}s`,
            // 필요한 VU: 대략 RPS * (평균RT초) 로 넉넉히
            preAllocatedVUs: Number(__ENV.PRE_VUS || Math.max(10, RPS * 2)),
            maxVUs:        Number(__ENV.MAX_VUS || Math.max(20, RPS * 4)),
        },
    },
    // page 태그별로 나눠보려면 대시보드에서 group by (tag:page)
    thresholds: {
        http_req_failed: ['rate<0.01'],
        'http_req_duration{page:*}': ['p(95)<3000'], // 태그 필터 예시
    },
    // 요약 통계 보기 편하게
    summaryTrendStats: ['count','min','avg','p(90)','p(95)','p(99)','max'],
};

// 현재 테스트 경과 시간(초) → 현재 페이지 계산
function currentPage() {
    const t = exec.scenario.iterationInTest / RPS; // arrival-rate: 초당 RPS개 만큼 iteration 증가
    const stepIndex = Math.floor(t / stepSec);     // 몇 번째 스텝인지
    const page = START_PAGE + stepIndex;
    return page <= END_PAGE ? page : END_PAGE;
}

export default function () {
    const page = currentPage();

    const params = {
        tags: { page: String(page) } // 페이지별로 메트릭 분리
    };

    // 필요한 경우 brandId 포함
    let url = `${BASE}/api/v1/products?sortType=${SORT}&size=${SIZE}&page=${page}`;
    if (BRAND_ID !== '') {
        url += `&brandId=${BRAND_ID}`;
    }

    const res = http.get(url);
    check(res, { 'status 200': (r) => r.status === 200 });
}
