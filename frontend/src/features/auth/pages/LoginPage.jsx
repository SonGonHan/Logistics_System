import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';
import { useAuth } from '../../../shared/auth/AuthContext';
import PasswordInput from '../../../shared/ui/PasswordInput';

export default function LoginPage() {
    const nav = useNavigate();
    const { setTokens } = useAuth();
    const [phone, setPhone] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    async function onSubmit(e) {
        e.preventDefault();
        setError(null);
        setLoading(true);
        try {
            const tokens = await authApi.signIn({ phone, password, email: null });
            setTokens(tokens.accessToken, tokens.refreshToken);
            nav('/profile');
        } catch (err) {
            setError(err?.payload?.message || err?.payload?.error || err.message);
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
                        placeholder="+79991234567"
                        autoComplete="tel"
                    />
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
                </div>

                <button disabled={loading} type="submit">
                    {loading ? 'Загрузка...' : 'Войти'}
                </button>
            </form>
            <div style={{ marginTop: 12 }}>
                Нет аккаунта? <Link to="/register">Зарегистрироваться</Link>
            </div>
        </div>
    );
}
