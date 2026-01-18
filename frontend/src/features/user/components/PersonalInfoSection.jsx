import React, { useState, useEffect } from 'react';
import { userApi } from '../api/userApi';

export default function PersonalInfoSection({ user, onUpdate }) {
    const [form, setForm] = useState({
        firstName: '',
        lastName: '',
        middleName: '',
        email: ''
    });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (user) {
            setForm({
                firstName: user.firstName || '',
                lastName: user.lastName || '',
                middleName: user.middleName || '',
                email: user.email || ''
            });
        }
    }, [user]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const updatedUser = await userApi.updatePersonalInfo(form);
            onUpdate(updatedUser);
            alert('Данные сохранены');
        } catch (err) {
            alert('Ошибка сохранения: ' + (err.response?.data?.message || err.message));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="profile-section">
            <h3>Личные данные</h3>
            <form onSubmit={handleSubmit}>
                <div className="form-row">
                    <div className="form-group">
                        <label>Фамилия</label>
                        <input
                            value={form.lastName}
                            onChange={e => setForm({...form, lastName: e.target.value})}
                        />
                    </div>
                    <div className="form-group">
                        <label>Имя</label>
                        <input
                            value={form.firstName}
                            onChange={e => setForm({...form, firstName: e.target.value})}
                        />
                    </div>
                    <div className="form-group">
                        <label>Отчество</label>
                        <input
                            value={form.middleName}
                            onChange={e => setForm({...form, middleName: e.target.value})}
                        />
                    </div>
                </div>

                <div className="form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        value={form.email}
                        onChange={e => setForm({...form, email: e.target.value})}
                    />
                </div>

                <button type="submit" disabled={loading}>Сохранить изменения</button>
            </form>
        </div>
    );
}
