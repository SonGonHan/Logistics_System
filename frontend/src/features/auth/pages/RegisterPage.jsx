import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { smsApi } from "../api/smsApi";
import PasswordInput from '../../../shared/ui/PasswordInput';

export default function RegisterPage() {
    const nav = useNavigate();

    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [password, setPassword] = useState("");

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [middleName, setMiddleName] = useState("");

    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    async function onSubmit(e) {
        e.preventDefault();
        setError(null);
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
            setError(err?.payload?.message || err?.payload?.error || err.message);
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
                </div>

                <div className="field">
                    <label>Телефон</label>
                    <input
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        placeholder="79991234567"
                        autoComplete="tel"
                    />
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
                </div>

                <div className="row">
                    <div className="field" style={{ flex: 1 }}>
                        <label>Имя</label>
                        <input value={firstName} onChange={(e) => setFirstName(e.target.value)} />
                    </div>
                    <div className="field" style={{ flex: 1 }}>
                        <label>Фамилия</label>
                        <input value={lastName} onChange={(e) => setLastName(e.target.value)} />
                    </div>
                </div>

                <div className="field">
                    <label>Отчество (необязательно)</label>
                    <input value={middleName} onChange={(e) => setMiddleName(e.target.value)} />
                </div>

                <button disabled={loading} type="submit">
                    Зарегистрироваться
                </button>
            </form>

            <div style={{ marginTop: 12 }}>
                Уже есть аккаунт? <Link to="/login">Войти</Link>
            </div>
        </div>
    );
}
