import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { smsApi } from "../api/smsApi";
import { authApi } from "../api/authApi";
import { useAuth } from "../../../shared/auth/AuthContext";

export default function ConfirmPhonePage() {
    const nav = useNavigate();
    const location = useLocation();
    const { setTokens } = useAuth();

    const phone = useMemo(() => location?.state?.payload?.phone ?? "", [location]);
    const [code, setCode] = useState("");

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    // Берём cooldown из sms/config, fallback 60. [file:2]
    const [cooldownSeconds, setCooldownSeconds] = useState(60);

    // Таймер: храним "когда снова можно отправить" и считаем оставшиеся секунды. [file:2]
    const [resendUntil, setResendUntil] = useState(0);
    const [now, setNow] = useState(Date.now());

    const resendLeft = useMemo(() => {
        return Math.max(0, Math.ceil((resendUntil - now) / 1000));
    }, [resendUntil, now]);

    useEffect(() => {
        if (resendUntil <= Date.now()) return;
        const id = setInterval(() => setNow(Date.now()), 250);
        return () => clearInterval(id);
    }, [resendUntil]);

    // Грузим sms/config один раз при открытии страницы. [file:2]
    useEffect(() => {
        let mounted = true;

        smsApi
            .getSmsConfig()
            .then((cfg) => {
                const seconds = cfg?.resendCooldownSeconds;
                if (mounted && Number.isFinite(seconds) && seconds > 0) {
                    setCooldownSeconds(seconds);
                }
            })
            .catch(() => {
                // fallback: 60
            });

        return () => {
            mounted = false;
        };
    }, []);

    // На страницу попадаем после отправки кода на шаге регистрации — запускаем таймер. [file:2]
    useEffect(() => {
        if (!phone) return;
        setResendUntil(Date.now() + cooldownSeconds * 1000);
    }, [phone, cooldownSeconds]);

    const startCooldown = (seconds) => {
        const s = Number(seconds);
        if (!Number.isFinite(s) || s <= 0) return;
        setResendUntil(Date.now() + s * 1000);
    };

    if (!location?.state || location.state.mode !== "register") {
        return (
            <div className="container">
                <h1>Подтверждение телефона</h1>
                <div className="error">Неверный сценарий. Вернитесь на регистрацию.</div>
            </div>
        );
    }

    async function onVerify(e) {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            await smsApi.verifyCode(phone, code);
            const tokens = await authApi.register(location.state.payload);
            setTokens(tokens.accessToken, tokens.refreshToken);
            nav("/profile");
        } catch (err) {
            setError(err?.payload?.message || err?.payload?.error || err.message);
        } finally {
            setLoading(false);
        }
    }

    async function onResendClick() {
        if (loading || resendLeft > 0) return;

        setError(null);
        setLoading(true);

        try {
            await smsApi.sendCode(phone);
            startCooldown(cooldownSeconds);
        } catch (err) {
            // Если вдруг словим 429 — просто перезапустим таймер на cooldownSeconds.
            if (err?.payload?.error === "RATELIMITEXCEEDED") {
                startCooldown(cooldownSeconds);
            }
            setError(err?.payload?.message || err?.payload?.error || err.message);
        } finally {
            setLoading(false);
        }
    }

    const resendText =
        resendLeft > 0
            ? `Отправить код снова можно через ${resendLeft} сек.`
            : "Отправить код снова";

    return (
        <div className="container">
            <h1>Подтверждение телефона</h1>

            <div style={{ marginBottom: 12 }}>
                На номер <b>{phone}</b> отправлен SMS-код.
            </div>

            {error && <div className="error">{error}</div>}

            <form onSubmit={onVerify}>
                <div className="field">
                    <label>Код из SMS</label>
                    <input
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        placeholder="123456"
                        autoComplete="one-time-code"
                        disabled={loading}
                    />
                </div>

                {/* Строка между инпутом и кнопкой "Подтвердить" */}
                <div style={{ margin: "10px 0" }}>
                    {resendLeft > 0 ? (
                        <div style={{ fontSize: 14, opacity: 0.85 }}>{resendText}</div>
                    ) : (
                        <button
                            type="button"
                            className="link"
                            onClick={onResendClick}
                            disabled={loading}
                            // classname="link"
                        >
                            {resendText}
                        </button>
                    )}
                </div>

                <button disabled={loading || !code} type="submit">
                    {loading ? "Загрузка..." : "Подтвердить"}
                </button>
            </form>
        </div>
    );
}
