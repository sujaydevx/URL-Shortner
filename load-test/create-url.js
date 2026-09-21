import http from "k6/http";
import { check } from "k6";

export const options = {
    vus: 100,
    duration: "30s",
};

const BASE_URL = "http://localhost:8080/u";

export default function () {

    const payload = JSON.stringify({
        originalUrl: "https://youtube.com",
        expiryOption: "SEVEN_DAYS",
    });

    const params = {
        headers: {
            "Content-Type": "application/json",
        },
    };

    const response = http.post(
        BASE_URL,
        payload,
        params
    );

    check(response, {
        "status is 201": (r) => r.status === 201,
    });
}