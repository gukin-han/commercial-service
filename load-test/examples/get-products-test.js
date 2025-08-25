import http from 'k6/http';

const BASE = __ENV.TARGET || 'http://host.docker.internal:8080';

export const options = {
    scenarios: {
        twenty_once: {
            executor: 'per-vu-iterations',
            vus: 20,
            iterations: 1,
            maxDuration: '1m',
        },
    },
};

export default function () {
    const sortType = 'LIKES_DESC';
    const size = 20;
    const brandId = 1000;

    const url = `${BASE}/api/v1/products?sortType=${sortType}&size=${size}`&`brandId=${brandId}`;

    http.get(url);
}