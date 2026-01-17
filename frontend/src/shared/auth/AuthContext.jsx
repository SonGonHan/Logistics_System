import React, { createContext, useContext, useMemo, useState } from "react";
import { tokenStorage } from "./tokenStorage";
import { authApi } from "../../features/auth/api/authApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [accessToken, setAccessToken] = useState(tokenStorage.getAccessToken());

    const value = useMemo(() => {
        const isAuthenticated = Boolean(accessToken);

        return {
            isAuthenticated,
            accessToken,

            setTokens: (at, rt) => {
                tokenStorage.setTokens(at, rt);
                setAccessToken(at);
            },

            clearLocal: () => {
                tokenStorage.clear();
                setAccessToken(null);
            },

            logout: async () => {
                const refreshToken = tokenStorage.getRefreshToken();
                try {
                    if (refreshToken) {
                        // POST /auth/logout {refreshToken} [file:21]
                        await authApi.logout(refreshToken);
                    }
                } finally {
                    tokenStorage.clear();
                    setAccessToken(null);
                }
            },
        };
    }, [accessToken]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth must be used within AuthProvider");
    return ctx;
}
