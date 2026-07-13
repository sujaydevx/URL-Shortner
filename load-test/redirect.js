import http from "k6/http";
import { check } from "k6";
import { SharedArray } from "k6/data";

export const options = {
    vus: 100,
    duration: "30s",
};

const shortCodes = new SharedArray("urls", function () {
    return JSON.parse(open("./urls.json"));
});

const BASE_URL = "http://localhost:8080/api/v1/urls";

const HOT_PERCENTAGE = 0.20;

const hotUrls = shortCodes.slice(
    0,
    Math.floor(shortCodes.length * HOT_PERCENTAGE)
);

const coldUrls = shortCodes.slice(
    Math.floor(shortCodes.length * HOT_PERCENTAGE)
);

export default function () {

    let selectedArray;

    if (Math.random() < 0.80) {
        selectedArray = hotUrls;
    } else {
        selectedArray = coldUrls;
    }

    const shortCode =
        selectedArray[Math.floor(Math.random() * selectedArray.length)];

    const response = http.get(
        `${BASE_URL}/${shortCode}`,
        {
            redirects: 0
        }
    );

    check(response, {
        "status is 302": (r) => r.status === 302,
    });
}