import React, { useState } from 'react';
import { userApi } from '../api/userApi';
import PasswordInput from '../../../shared/ui/PasswordInput'; // Наш компонент с "глазиком"

export default function ChangePasswordSection() {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null); // success или error

    const handleChangePassword = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);
        try {
            await userApi.updatePassword({ oldPassword, newPassword });
            setMessage({ type: 'success', text: 'Пароль успешно обновлен' });
            setOldPassword('');
            setNewPassword('');
        } catch (err) {
            setMessage({ type: 'error', text: err.response?.data?.message || 'Ошибка' });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="profile-section">
            <h3>Безопасность</h3>
            <form onSubmit={handleChangePassword}>
                {message && (
                    <div className={message.type === 'error' ? 'error-message' : 'success-message'}>
                        {message.text}
                    </div>
                )}
                <div className="form-group">
                    <label>Текущий пароль</label>
                    <PasswordInput
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        disabled={loading}
                    />
                </div>
                <div className="form-group">
                    <label>Новый пароль</label>
                    <PasswordInput
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        disabled={loading}
                    />
                </div>
                <button type="submit" disabled={loading || !oldPassword || !newPassword}>
                    Обновить пароль
                </button>
            </form>
        </div>
    );
}
