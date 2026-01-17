import { http } from "../../../shared/http/http";

export const authApi = {
    // POST /auth/sign-in
    signIn(body) {
        return http("/auth/sign-in", {
            method: "POST",
            body,
            withAuth: false,
        });
    },

    // POST /auth/register
    register(body) {
        return http("/auth/register", {
            method: "POST",
            body,
            withAuth: false,
        });
    },

    // POST /auth/refresh
    refresh(refreshToken) {
        return http("/auth/refresh", {
            method: "POST",
            body: { refreshToken },
            withAuth: false,
        });
    },

    // POST /auth/logout
    logout(refreshToken) {
        return http("/auth/logout", {
            method: "POST",
            body: { refreshToken },
            withAuth: false,
        });
    },
};
