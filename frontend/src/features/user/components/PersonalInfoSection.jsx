import React, { useState, useEffect } from "react";
import { userApi } from "../api/userApi";

export default function PersonalInfoSection({ user, onUpdate }) {
    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        middleName: "",
        email: "",
    });
    const [loading, setLoading] = useState(false);
    const [fieldErrors, setFieldErrors] = useState({});
    const [message, setMessage] = useState(null);

    useEffect(() => {
        if (user) {
            setForm({
                firstName: user.firstName || "",
                lastName: user.lastName || "",
                middleName: user.middleName || "",
                email: user.email || "",
            });
        }
    }, [user]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setFieldErrors({});
        setMessage(null);

        try {
            const updatedUser = await userApi.updatePersonalInfo(form);
            onUpdate(updatedUser);
            setMessage({ type: "success", text: "Данные успешно обновлены" });
        } catch (err) {
            if (err?.payload?.error === "VALIDATION_FAILED" && err?.payload?.fields) {
                setFieldErrors(err.payload.fields);
                setMessage({ type: "error", text: "Исправьте ошибки в форме" });
            } else {
                setMessage({
                    type: "error",
                    text: err.response?.data?.message || err?.payload?.message || err.message
                });
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="profile-section">
            <h3>Персональные данные</h3>
            <form onSubmit={handleSubmit}>
                {message && (
                    <div className={message.type === "error" ? "error-message" : "success-message"}>
                        {message.text}
                    </div>
                )}

                <div className="form-row">
                    <div className="form-group">
                        <label>Фамилия</label>
                        <input
                            value={form.lastName}
                            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
                        />
                        {fieldErrors.lastName && <div className="field-error">{fieldErrors.lastName}</div>}
                    </div>
                    <div className="form-group">
                        <label>Имя</label>
                        <input
                            value={form.firstName}
                            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
                        />
                        {fieldErrors.firstName && <div className="field-error">{fieldErrors.firstName}</div>}
                    </div>
                    <div className="form-group">
                        <label>Отчество</label>
                        <input
                            value={form.middleName}
                            onChange={(e) => setForm({ ...form, middleName: e.target.value })}
                        />
                        {fieldErrors.middleName && <div className="field-error">{fieldErrors.middleName}</div>}
                    </div>
                </div>

                <div className="form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        value={form.email}
                        onChange={(e) => setForm({ ...form, email: e.target.value })}
                    />
                    {fieldErrors.email && <div className="field-error">{fieldErrors.email}</div>}
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? "Сохранение..." : "Сохранить изменения"}
                </button>
            </form>
        </div>
    );
}
