import http from 'k6/http';

const BASE = __ENV.TARGET || 'http://host.docker.internal:8080';

export const options = {
    scenarios: {
        rps_10: {
            executor: 'constant-arrival-rate',
            rate: 10,           // 초당 200 요청 = 목표 throughput
            timeUnit: '1s',
            duration: '2m',      // 최소 1~2분은 돌려야 그래프가 안정됨
            preAllocatedVUs: 100, // 예상 동시성보다 여유 있게
            maxVUs: 400,
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],                // 실패율 < 1%
        http_req_duration: ['p(50)<200', 'p(95)<500', 'p(99)<900'], // 지표 가드
    },
    summaryTrendStats: ['count','min','med','p(90)','p(95)','p(99)','max'],
};

export default function () {
    const sortType = 'LIKES_DESC';
    const size = 20;

    const url = `${BASE}/api/v1/products?sortType=${sortType}&size=${size}`;

    http.get(url);
}