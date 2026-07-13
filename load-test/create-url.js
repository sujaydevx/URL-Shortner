import http from 'k6/http';

import { check } from 'k6';

export const options = {
    vus: 100,
    duration: '30s',
};

export default function () {

    const payload = JSON.stringify({
        originalUrl: "https://youtube.com",
        expiryOption: "SEVEN_DAYS"
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(
        'http://localhost:8080/api/v1/urls',
        payload,
        params
    );

    check(res, {
        'status is 201': (r) => r.status === 201,
    });

}