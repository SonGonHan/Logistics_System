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

const ACCEPTANCE = "/acceptance";

/**
 * Start acceptance by scanning a barcode.
 * POST /acceptance/start
 * @param {{ barcode: string, facilityId: number }} data
 * @returns {Promise<AcceptanceResponse>}
 */
export const startAcceptance = async (data) => {
    return await coreHttp(`${ACCEPTANCE}/start`, "POST", data);
};

/**
 * Complete acceptance and create a waybill.
 * POST /acceptance/{draftId}/complete
 * @param {number} draftId
 * @param {{ draftId: number, facilityId: number }} data
 * @returns {Promise<CompleteAcceptanceResponse>}
 */
export const completeAcceptance = async (draftId, data) => {
    return await coreHttp(`${ACCEPTANCE}/${draftId}/complete`, "POST", data);
};
