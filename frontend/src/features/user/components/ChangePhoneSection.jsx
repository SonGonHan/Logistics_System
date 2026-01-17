import React, { useState } from 'react';
import { userApi } from '../api/userApi';
import { smsApi } from '../../auth/api/smsApi';

export default function ChangePhoneSection({ currentPhone, onPhoneUpdated }) {
    const [isEditing, setIsEditing] = useState(false);
    const [newPhone, setNewPhone] = useState('');
    const [code, setCode] = useState('');
    const [step, setStep] = useState('input'); // 'input' | 'verify'
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    // Сброс формы при начале редактирования
    const startEditing = () => {
        setIsEditing(true);
        setNewPhone('');
        setCode('');
        setStep('input');
        setError(null);
    };

    // Отмена редактирования
    const cancel = () => {
        setIsEditing(false);
        setError(null);
    };

    // 1. Отправка кода на новый номер
    const handleSendCode = async () => {
        if (!newPhone) return;

        setError(null);
        setLoading(true);
        try {
            await smsApi.sendCode(newPhone);  // ✅ Исправлено
            setStep('verify');
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || 'Ошибка отправки SMS. Проверьте формат номера.');
        } finally {
            setLoading(false);
        }
    };

// 2. Проверка кода и сохранение нового номера
    const handleVerifyAndSave = async () => {
        if (!code) return;

        setError(null);
        setLoading(true);
        try {
            await smsApi.verifyCode(newPhone, code);

            await userApi.updateProfile({ phone: newPhone });

            onPhoneUpdated(newPhone);
            setIsEditing(false);
            alert('Телефон успешно изменен!');
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || 'Неверный код или ошибка при смене номера');
        } finally {
            setLoading(false);
        }
    };


    if (!isEditing) {
        return (
            <div className="profile-section">
                <h3>Телефон</h3>
                <div className="phone-display">
                    <span>{currentPhone || 'Не указан'}</span>
                    <button className="btn-secondary" onClick={startEditing}>
                        Изменить номер
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="profile-section editing">
            <h3>Смена телефона</h3>

            {error && <div className="error">{error}</div>}

            {step === 'input' ? (
                // Шаг 1: Ввод нового номера
                <div className="form-group">
                    <label>Новый номер</label>
                    <input
                        type="tel"
                        value={newPhone}
                        onChange={(e) => setNewPhone(e.target.value)}
                        placeholder="+79990000000"
                        disabled={loading}
                    />
                    <div className="actions">
                        <button onClick={handleSendCode} disabled={loading || !newPhone}>
                            {loading ? 'Отправка...' : 'Получить код'}
                        </button>
                        <button className="btn-text" onClick={cancel} disabled={loading}>
                            Отмена
                        </button>
                    </div>
                </div>
            ) : (
                // Шаг 2: Ввод кода подтверждения
                <div className="form-group">
                    <label>Код из SMS (отправлен на {newPhone})</label>
                    <input
                        type="text"
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        placeholder="123456"
                        disabled={loading}
                    />
                    <div className="actions">
                        <button onClick={handleVerifyAndSave} disabled={loading || !code}>
                            {loading ? 'Проверка...' : 'Подтвердить'}
                        </button>
                        <button
                            className="btn-text"
                            onClick={() => setStep('input')}
                            disabled={loading}
                        >
                            Назад
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
