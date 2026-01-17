import React, { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { smsApi } from "../api/smsApi";
import { authApi } from "../api/authApi";
import { useAuth } from "../../../shared/auth/AuthContext";

export default function ConfirmPhonePage() {
    const nav = useNavigate();
    const { state } = useLocation();
    const { setTokens } = useAuth();

    const phone = useMemo(() => state?.payload?.phone ?? "", [state]);

    const [code, setCode] = useState("");
    const [info, setInfo] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    if (!state || state.mode !== "register") {
        return (
            <div className="container">
                <h1>Подтверждение телефона</h1>
                <div className="error">
                    Нет данных регистрации. Вернись на страницу регистрации.
                </div>
            </div>
        );
    }

    async function onVerify(e) {
        e.preventDefault();
        setError(null);
        setInfo(null);
        setLoading(true);

        try {
            await smsApi.verifyCode(phone, code);

            // POST /auth/register
            const tokens = await authApi.register(state.payload);
            setTokens(tokens.accessToken, tokens.refreshToken);

            nav("/profile");
        } catch (err) {
            setError(err?.payload?.message || err?.payload?.error || err.message);
        } finally {
            setLoading(false);
        }
    }

    async function onResend() {
        setError(null);
        setInfo(null);
        setLoading(true);
        try {
            await smsApi.resendCode(phone);
            setInfo("Код отправлен повторно.");
        } catch (err) {
            setError(err?.payload?.message || err?.payload?.error || err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="container">
            <h1>Подтверждение номера</h1>
            <div style={{ marginBottom: 12 }}>
                На номер <b>{phone}</b> отправлен код.
            </div>

            {error && <div className="error">{error}</div>}
            {info && <div className="success">{info}</div>}

            <form onSubmit={onVerify}>
                <div className="field">
                    <label>Код из SMS</label>
                    <input
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        placeholder="123456"
                        autoComplete="one-time-code"
                    />
                </div>

                <button disabled={loading} type="submit">
                    Подтвердить
                </button>
            </form>

            <div style={{ marginTop: 12 }}>
                <button className="link" onClick={onResend} disabled={loading}>
                    Отправить код ещё раз
                </button>
            </div>
        </div>
    );
}
