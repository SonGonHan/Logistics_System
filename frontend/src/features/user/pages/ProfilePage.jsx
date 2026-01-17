import React, { useEffect, useState } from 'react';
import { userApi } from '../api/userApi';
import ChangePhoneSection from '../components/ChangePhoneSection';
import ChangePasswordSection from '../components/ChangePasswordSection';
import PersonalInfoSection from '../components/PersonalInfoSection';
import './ProfilePage.css'; // Не забудьте стили

export default function ProfilePage() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    // Загрузка данных пользователя
    const loadUser = async () => {
        try {
            const data = await userApi.getProfile();
            setUser(data);
        } catch (err) {
            console.error('Failed to load profile', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadUser();
    }, []);

    const handleUserUpdate = (updatedData) => {
        setUser(prev => ({ ...prev, ...updatedData }));
    };

    if (loading) return <div className="container">Загрузка...</div>;
    if (!user) return <div className="container">Ошибка загрузки профиля</div>;

    return (
        <div className="container profile-page">
            <h1>Настройки профиля</h1>

            {/* 1. Секция телефона */}
            <ChangePhoneSection
                currentPhone={user.phone}
                onPhoneUpdated={(phone) => handleUserUpdate({ phone })}
            />

            <hr className="divider" />

            {/* 2. Секция пароля */}
            <ChangePasswordSection />

            <hr className="divider" />

            {/* 3. Секция личных данных */}
            <PersonalInfoSection
                user={user}
                onUpdate={handleUserUpdate}
            />
        </div>
    );
}
