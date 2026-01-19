import React, { useEffect, useMemo, useState } from "react";
import { userApi } from "../api/userApi";
import { smsApi } from "../../auth/api/smsApi";

export default function ChangePhoneSection({ currentPhone, onPhoneUpdated }) {
    const [isEditing, setIsEditing] = useState(false);
    const [newPhone, setNewPhone] = useState("");
    const [code, setCode] = useState("");
    const [step, setStep] = useState("input"); // "input" | "verify"

    const [error, setError] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const [cooldownSeconds, setCooldownSeconds] = useState(60);

    // Таймер: храним "время, когда можно снова отправить", а оставшиеся секунды считаем от Date.now().
    const [resendUntil, setResendUntil] = useState(0);
    const [now, setNow] = useState(Date.now());

    const resendLeft = useMemo(() => {
        return Math.max(0, Math.ceil((resendUntil - now) / 1000));
    }, [resendUntil, now]);

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
                // fallback: оставляем 60
            });

        return () => {
            mounted = false;
        };
    }, []);

    useEffect(() => {
        if (resendUntil <= Date.now()) return;
        const id = setInterval(() => setNow(Date.now()), 250);
        return () => clearInterval(id);
    }, [resendUntil]);

    const isValidationError = (err) => {
        const code = err?.payload?.error;
        // В бэке встречается VALIDATIONFAILED (без underscore).
        return (code === "VALIDATIONFAILED" || code === "VALIDATION_FAILED") && err?.payload?.fields;
    };

    const parseSecondsFromMessage = (msg) => {
        const s = String(msg || "");
        let m = s.match(/(\d+)\s*сек/i);
        if (m) return Number(m[1]);
        m = s.match(/(\d+)/);
        return m ? Number(m[1]) : null;
    };

    const startCooldown = (seconds) => {
        const s = Number(seconds);
        if (!Number.isFinite(s) || s <= 0) return;
        setResendUntil(Date.now() + s * 1000);
    };

    const clearFlow = () => {
        setIsEditing(false);
        setNewPhone("");
        setCode("");
        setStep("input");
        setError(null);
        setFieldErrors({});
        setResendUntil(0);
    };

    const handleSendCode = async () => {
        setError(null);
        setFieldErrors({});
        setLoading(true);

        try {
            await smsApi.sendCode(newPhone);
            setStep("verify");
            startCooldown(cooldownSeconds);
        } catch (err) {
            if (err?.payload?.error === "RATELIMITEXCEEDED") {
                const seconds = parseSecondsFromMessage(err?.payload?.message) ?? cooldownSeconds;
                startCooldown(seconds);
            }

            if (isValidationError(err)) {
                setFieldErrors(err.payload.fields);
                setError("Исправьте ошибки");
            } else {
                setError(err?.payload?.message || err?.payload?.error || err.message);
            }
        } finally {
            setLoading(false);
        }
    };

    const handleResendCode = async () => {
        if (loading || resendLeft > 0) return;

        setError(null);
        setFieldErrors({});
        setLoading(true);

        try {
            // Повтор — тем же методом отправки
            await smsApi.sendCode(newPhone);
            startCooldown(cooldownSeconds);
        } catch (err) {
            if (err?.payload?.error === "RATELIMITEXCEEDED") {
                const seconds = parseSecondsFromMessage(err?.payload?.message) ?? cooldownSeconds;
                startCooldown(seconds);
            }
            setError(err?.payload?.message || err?.payload?.error || err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleVerifyAndSave = async () => {
        setError(null);
        setFieldErrors({});
        setLoading(true);

        try {
            await smsApi.verifyCode(newPhone, code);
            const updated = await userApi.updatePhone({ phone: newPhone });
            onPhoneUpdated?.(updated);
            clearFlow();
        } catch (err) {
            if (isValidationError(err)) {
                setFieldErrors(err.payload.fields);
                setError("Исправьте ошибки");
            } else {
                setError(err?.payload?.message || err?.payload?.error || err.message);
            }
        } finally {
            setLoading(false);
        }
    };

    const cancel = () => clearFlow();

    const backToInput = () => {
        setStep("input");
        setCode("");
        setError(null);
        setFieldErrors({});
        setResendUntil(0);
    };

    return (
        <div className="profile-section">
            <h3>Смена телефона</h3>

            {!isEditing ? (
                <div>
                    <p>
                        Текущий номер: <b>{currentPhone}</b>
                    </p>
                    <button onClick={() => setIsEditing(true)} type="button">
                        Изменить
                    </button>
                </div>
            ) : (
                <div>
                    {error && <div className="error">{error}</div>}

                    {step === "input" ? (
                        <div>
                            <div className="form-group">
                                <label>Новый номер</label>
                                <input
                                    type="tel"
                                    value={newPhone}
                                    onChange={(e) => setNewPhone(e.target.value)}
                                    placeholder="79990000000"
                                    disabled={loading}
                                />
                                {fieldErrors.phone && <div className="field-error">{fieldErrors.phone}</div>}
                            </div>

                            <div className="actions">
                                <button onClick={handleSendCode} disabled={loading || !newPhone} type="button">
                                    {loading ? "Загрузка..." : "Отправить код"}
                                </button>

                                <button className="btn-text" onClick={cancel} disabled={loading} type="button">
                                    Отмена
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div>
                            <div className="form-group">
                                <label>SMS на {newPhone}</label>
                                <input
                                    type="text"
                                    value={code}
                                    onChange={(e) => setCode(e.target.value)}
                                    placeholder="123456"
                                    disabled={loading}
                                />
                                {fieldErrors.code && <div className="field-error">{fieldErrors.code}</div>}
                            </div>

                            {/* Вместо кнопки: отсчёт / кликабельный текст */}
                            <div style={{ margin: "10px 0 12px", fontSize: 14 }}>
                                {resendLeft > 0 ? (
                                    <span>Отправить код снова можно через {resendLeft} сек.</span>
                                ) : (
                                    <button
                                        type="button"
                                        className="link" // ты уже переписал .link
                                        onClick={handleResendCode}
                                        disabled={loading}
                                        style={{ padding: 0 }}
                                    >
                                        Отправить код снова
                                    </button>
                                )}
                            </div>

                            <div className="actions">
                                <button onClick={handleVerifyAndSave} disabled={loading || !code} type="button">
                                    {"Подтвердить"}
                                </button>

                                <button className="btn-text" onClick={backToInput} disabled={loading} type="button">
                                    Назад
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}
