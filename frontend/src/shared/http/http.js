import { config } from "../config";
import { tokenStorage } from "../auth/tokenStorage";

async function rawFetch(path, options) {
    const { method, body, withAuth } = options;

    const headers = {
        "Content-Type": "application/json",
    };

    if (withAuth) {
        const accessToken = tokenStorage.getAccessToken();
        if (accessToken) headers.Authorization = `Bearer ${accessToken}`; // [file:21]
    }

    const res = await fetch(`${config.apiBaseUrl}${path}`, {
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

async function refreshTokens() {
    const refreshToken = tokenStorage.getRefreshToken();
    if (!refreshToken) throw new Error("No refresh token");

    // POST /auth/refresh {refreshToken} [file:21]
    const tokens = await rawFetch("/auth/refresh", {
        method: "POST",
        body: { refreshToken },
        withAuth: false,
    });

    tokenStorage.setTokens(tokens.accessToken, tokens.refreshToken);
    return tokens;
}

export async function http(path, options) {
    try {
        return await rawFetch(path, options);
    } catch (e) {
        // Если протух access token — пробуем refresh и повторяем запрос [file:21]
        if (options.withAuth && e?.status === 401) {
            await refreshTokens();
            return await rawFetch(path, options);
        }
        throw e;
    }
}
