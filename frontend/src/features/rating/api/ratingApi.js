import { config } from "../../../shared/config";
import { tokenStorage } from "../../../shared/auth/tokenStorage";

async function coreHttp(path, method = "GET", body = null) {
    const headers = { "Content-Type": "application/json" };
    const accessToken = tokenStorage.getAccessToken();
    if (accessToken) headers.Authorization = `Bearer ${accessToken}`;

    const res = await fetch(`${config.coreBusinessApiUrl}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    const text = await res.text();
    const data = text ? JSON.parse(text) : null;

    if (!res.ok) {
        const err = new Error(data?.message || data?.error || `HTTP ${res.status}`);
        err.status = res.status;
        err.payload = data;
        throw err;
    }

    return data;
}

const RATINGS = "/ratings";

/**
 * Submit a rating for a delivered waybill.
 * POST /ratings/submit
 * @param {{ waybillId: number, score: number, comment?: string }} data
 * @returns {Promise<RatingResponse>}
 */
export const submitRating = async (data) => {
    return await coreHttp(`${RATINGS}/submit`, "POST", data);
};

/**
 * Get rating for a specific waybill.
 * GET /ratings/waybill/{waybillId}
 * @param {number} waybillId
 * @returns {Promise<RatingResponse>}
 */
export const getRatingByWaybill = async (waybillId) => {
    return await coreHttp(`${RATINGS}/waybill/${waybillId}`, "GET");
};
