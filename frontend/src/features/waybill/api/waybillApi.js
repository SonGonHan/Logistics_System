import { config } from "../../../shared/config";
import { tokenStorage } from "../../../shared/auth/tokenStorage";

const WAYBILLS = "/waybills";

async function coreHttp(path, method = "GET", body = null) {
    const headers = { "Content-Type": "application/json" };
    const accessToken = tokenStorage.getAccessToken();
    if (accessToken) {
        headers.Authorization = `Bearer ${accessToken}`;
    }

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

export const createWaybill = async (data) => {
    return await coreHttp(WAYBILLS, "POST", data);
};

export const getWaybillById = async (id) => {
    return await coreHttp(`${WAYBILLS}/${id}`, "GET");
};

export const getUserWaybills = async () => {
    return await coreHttp(WAYBILLS, "GET");
};

export const updateWaybillStatus = async (id, statusData) => {
    return await coreHttp(`${WAYBILLS}/${id}/status`, "PUT", statusData);
};
