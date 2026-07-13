import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    duration: '30s',
};

const SHORT_CODE = "9Yc";

export default function () {

    const response = http.get(
        `http://localhost:8080/api/v1/urls/${SHORT_CODE}`,
        {
            redirects: 0
        }
    );

    check(response, {
        "status is 302": (r) => r.status === 302,
    });
}