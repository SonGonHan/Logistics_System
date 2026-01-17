import { http } from "../../../shared/http/http";

export const userApi = {
    // GET /users/me
    getProfile() {
        return http("/users/me", {
            method: "GET",
            withAuth: true, // Нужен токен
        });
    },

    // PUT /users/me (обновление данных и телефона, если бэк позволяет)
    updateProfile(body) {
        return http("/users/me", {
            method: "PUT",
            body,
            withAuth: true,
        });
    },

    // POST /users/me/password (или как у вас на бэке реализована смена пароля)
    // Если отдельного эндпоинта нет, удалите этот метод и используйте updateProfile
    updatePassword(body) {
        // Пример (уточните свой путь в Swagger/документации):
        return http("/users/me/password", {
            method: "POST", // или PUT
            body,
            withAuth: true,
        });
    }
};
