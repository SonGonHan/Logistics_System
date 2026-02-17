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

const DRAFTS = "/waybills/drafts";

export const createDraft = async (draftData) => {
    return await coreHttp(DRAFTS, "POST", draftData);
};

export const getUserDrafts = async (status = null) => {
    const path = status ? `${DRAFTS}?status=${encodeURIComponent(status)}` : DRAFTS;
    return await coreHttp(path, "GET");
};

export const getDraftById = async (id) => {
    return await coreHttp(`${DRAFTS}/${id}`, "GET");
};

export const getDraftByBarcode = async (barcode) => {
    return await coreHttp(`${DRAFTS}/by-barcode/${barcode}`, "GET");
};

export const updateDraft = async (id, draftData) => {
    return await coreHttp(`${DRAFTS}/${id}`, "PUT", draftData);
};

export const deleteDraft = async (id) => {
    await coreHttp(`${DRAFTS}/${id}`, "DELETE");
};

export const getPricingRules = async () => {
    return await coreHttp("/pricing-rules", "GET");
};