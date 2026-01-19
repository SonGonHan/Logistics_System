import React, { useState } from "react";
import { userApi } from "../api/userApi";
import PasswordInput from "../../../shared/ui/PasswordInput";

export default function ChangePasswordSection() {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});

    const handleChangePassword = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);
        setFieldErrors({});

        try {
            await userApi.updatePassword({ oldPassword, newPassword });
            setMessage({ type: "success", text: "Пароль успешно изменен" });
            setOldPassword("");
            setNewPassword("");
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
            <h3>Изменить пароль</h3>
            <form onSubmit={handleChangePassword}>
                {message && (
                    <div className={message.type === "error" ? "error-message" : "success-message"}>
                        {message.text}
                    </div>
                )}

                <div className="form-group">
                    <label>Старый пароль</label>
                    <PasswordInput
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        disabled={loading}
                    />
                    {fieldErrors.oldPassword && <div className="field-error">{fieldErrors.oldPassword}</div>}
                </div>

                <div className="form-group">
                    <label>Новый пароль</label>
                    <PasswordInput
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        disabled={loading}
                    />
                    {fieldErrors.newPassword && <div className="field-error">{fieldErrors.newPassword}</div>}
                </div>

                <button type="submit" disabled={loading || !oldPassword || !newPassword}>
                    {loading ? "Сохранение..." : "Изменить пароль"}
                </button>
            </form>
        </div>
    );
}
