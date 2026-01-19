import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { smsApi } from "../api/smsApi";
import PasswordInput from "../../../shared/ui/PasswordInput";

export default function RegisterPage() {
    const nav = useNavigate();
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [password, setPassword] = useState("");
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [middleName, setMiddleName] = useState("");
    const [error, setError] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);

    async function onSubmit(e) {
        e.preventDefault();
        setError(null);
        setFieldErrors({});
        setLoading(true);

        try {
            await smsApi.sendCode(phone);
            nav("/confirm-phone", {
                state: {
                    mode: "register",
                    payload: { email, phone, password, firstName, lastName, middleName },
                },
            });
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
            <h1>Регистрация</h1>
            {error && <div className="error">{error}</div>}

            <form onSubmit={onSubmit}>
                <div className="field">
                    <label>Email</label>
                    <input
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="user@example.com"
                        autoComplete="email"
                    />
                    {fieldErrors.email && <div className="field-error">{fieldErrors.email}</div>}
                </div>

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
                        autoComplete="new-password"
                        disabled={loading}
                    />
                    {fieldErrors.password && <div className="field-error">{fieldErrors.password}</div>}
                </div>

                <div className="row">
                    <div className="field" style={{ flex: 1 }}>
                        <label>Имя</label>
                        <input
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                        />
                        {fieldErrors.firstName && <div className="field-error">{fieldErrors.firstName}</div>}
                    </div>
                    <div className="field" style={{ flex: 1 }}>
                        <label>Фамилия</label>
                        <input
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                        />
                        {fieldErrors.lastName && <div className="field-error">{fieldErrors.lastName}</div>}
                    </div>
                </div>

                <div className="field">
                    <label>Отчество (необязательно)</label>
                    <input
                        value={middleName}
                        onChange={(e) => setMiddleName(e.target.value)}
                    />
                    {fieldErrors.middleName && <div className="field-error">{fieldErrors.middleName}</div>}
                </div>

                <button disabled={loading} type="submit">
                    {loading ? "Загрузка..." : "Продолжить"}
                </button>
            </form>

            <div style={{ marginTop: 12 }}>
                Уже есть аккаунт? <Link to="/login">Войти</Link>
            </div>
        </div>
    );
}
