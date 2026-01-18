import { http } from "../../../shared/http/http";

export const userApi = {
    getProfile() {
        return http("/users/me", {
            method: "GET",
            withAuth: true,
        });
    },

    updateProfile(body) {
        return http("/users/me", {
            method: "PUT",
            body,
            withAuth: true,
        });
    },

    updatePassword(body) {
        return http("/users/me/password", {
            method: "PUT",
            body,
            withAuth: true,
        });
    },

    updatePersonalInfo(body) {
        return http("/users/me/personal", {
            method: "PATCH",          // важно: PATCH, как на бэке
            body,                     // { firstName, lastName, middleName, email }
            withAuth: true,
        });
    },

    updatePhone(body) {
        return http("/users/me/phone", {
            method: "PUT",
            body,
            withAuth: true,
        });
    },
};
