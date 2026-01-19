import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../api/authApi";
import { useAuth } from "../../../shared/auth/AuthContext";
import PasswordInput from "../../../shared/ui/PasswordInput";

export default function LoginPage() {
    const nav = useNavigate();
    const { setTokens } = useAuth();
    const [phone, setPhone] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);

    async function onSubmit(e) {
        e.preventDefault();
        setError(null);
        setFieldErrors({});
        setLoading(true);

        try {
            const tokens = await authApi.signIn({ phone, password, email: null });
            setTokens(tokens.accessToken, tokens.refreshToken);
            nav("/profile");
        } catch (err) {
            if (err?.payload?.error === "VALIDATION_FAILED" && err?.payload?.fields) {
                setFieldErrors(err.payload.fields);
                setError("Пожалуйста, исправьте ошибки в форме");
            } else {
                setError(err?.payload?.message || err?.payload?.error || err.message);
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="container">
            <h1>Вход</h1>
            {error && <div className="error">{error}</div>}

            <form onSubmit={onSubmit}>
                <div className="field">
                    <label>Телефон</label>
                    <input
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        placeholder="79991234567"
                        autoComplete="tel"
                    />
                    {fieldErrors.phone && <div className="field-error">{fieldErrors.phone}</div>}
                </div>

                <div className="field">
                    <label>Пароль</label>
                    <PasswordInput
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password123!"
                        autoComplete="current-password"
                        disabled={loading}
                    />
                    {fieldErrors.password && <div className="field-error">{fieldErrors.password}</div>}
                </div>

                <button disabled={loading} type="submit">
                    {loading ? "Загрузка..." : "Войти"}
                </button>
            </form>

            <div style={{ marginTop: 12 }}>
                Нет аккаунта? <Link to="/register">Зарегистрироваться</Link>
            </div>
        </div>
    );
}
