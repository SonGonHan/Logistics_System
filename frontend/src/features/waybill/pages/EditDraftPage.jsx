import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getDraftById, updateDraft } from "../api/draftApi";
import "./CreateDraftPage.css";

export default function EditDraftPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    const [formData, setFormData] = useState({
        recipientUserId: "",
        recipientAddress: "",
        weightDeclared: "",
        pricingRuleId: "",
        length: "",
        width: "",
        height: "",
    });

    // Загрузка черновика при монтировании компонента
    useEffect(() => {
        loadDraft();
    }, [id]);

    const loadDraft = async () => {
        try {
            setLoading(true);
            setError(null);
            const draft = await getDraftById(id);

            // Заполнение формы данными из черновика
            setFormData({
                recipientUserId: draft.recipientUserId || "",
                recipientAddress: draft.recipientAddress || "",
                weightDeclared: draft.weightDeclared || "",
                pricingRuleId: "", // API не возвращает pricingRuleId в базовом ответе
                length: draft.dimensions?.length || "",
                width: draft.dimensions?.width || "",
                height: draft.dimensions?.height || "",
            });
        } catch (err) {
            setError(err.response?.data?.message || "Ошибка загрузки черновика");
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        // Валидация обязательных полей
        if (!formData.recipientUserId || !formData.recipientAddress || !formData.weightDeclared) {
            setError("Пожалуйста, заполните все обязательные поля");
            return;
        }

        // Проверка габаритов: либо все заполнены, либо все пусты
        const hasDimensions = formData.length || formData.width || formData.height;
        const allDimensionsFilled = formData.length && formData.width && formData.height;

        if (hasDimensions && !allDimensionsFilled) {
            setError("Габариты: заполните все три поля (длина, ширина, высота) или оставьте все пустыми");
            return;
        }

        try {
            setSaving(true);

            // Подготовка данных для отправки
            const draftData = {
                recipientUserId: Number(formData.recipientUserId),
                recipientAddress: formData.recipientAddress,
                weightDeclared: Number(formData.weightDeclared),
                pricingRuleId: formData.pricingRuleId ? Number(formData.pricingRuleId) : null,
                dimensions: allDimensionsFilled
                    ? {
                          length: Number(formData.length),
                          width: Number(formData.width),
                          height: Number(formData.height),
                      }
                    : null,
            };

            await updateDraft(id, draftData);
            navigate("/waybills");
        } catch (err) {
            setError(err.response?.data?.message || "Ошибка при обновлении черновика");
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return <div className="create-draft-container">Загрузка...</div>;
    }

    return (
        <div className="create-draft-container">
            <div className="create-draft-header">
                <h1>Редактирование черновика</h1>
                <button
                    className="create-draft-back-button"
                    onClick={() => navigate("/waybills")}
                >
                    Назад к списку
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            <form className="create-draft-form" onSubmit={handleSubmit}>
                {/* ID получателя */}
                <div className="form-group">
                    <label>
                        ID получателя <span className="create-draft-required">*</span>
                    </label>
                    <input
                        type="number"
                        name="recipientUserId"
                        value={formData.recipientUserId}
                        onChange={handleChange}
                        required
                    />
                    <div className="create-draft-hint">
                        Введите ID пользователя, который будет получателем
                    </div>
                </div>

                {/* Адрес доставки */}
                <div className="form-group">
                    <label>
                        Адрес доставки <span className="create-draft-required">*</span>
                    </label>
                    <textarea
                        name="recipientAddress"
                        className="create-draft-textarea"
                        value={formData.recipientAddress}
                        onChange={handleChange}
                        required
                        placeholder="Введите полный адрес доставки"
                    />
                </div>

                {/* Заявленный вес */}
                <div className="form-group">
                    <label>
                        Заявленный вес (кг) <span className="create-draft-required">*</span>
                    </label>
                    <input
                        type="number"
                        name="weightDeclared"
                        value={formData.weightDeclared}
                        onChange={handleChange}
                        step="0.01"
                        min="0.01"
                        required
                    />
                    <div className="create-draft-hint">Минимальный вес: 0.01 кг</div>
                </div>

                {/* ID правила ценообразования */}
                <div className="form-group">
                    <label>
                        ID правила ценообразования (опционально)
                    </label>
                    <input
                        type="number"
                        name="pricingRuleId"
                        value={formData.pricingRuleId}
                        onChange={handleChange}
                    />
                    <div className="create-draft-hint">
                        Если не указано, будет использовано правило по умолчанию
                    </div>
                </div>

                {/* Габариты (опционально) */}
                <div className="form-group">
                    <div className="create-draft-dimensions">
                        <div className="create-draft-dimensions-title">
                            Габариты посылки (см) — опционально
                        </div>
                        <div className="create-draft-dimensions-grid">
                            <div className="create-draft-dimension-field">
                                <label>Длина</label>
                                <input
                                    type="number"
                                    name="length"
                                    value={formData.length}
                                    onChange={handleChange}
                                    step="0.01"
                                    min="0.01"
                                />
                            </div>
                            <div className="create-draft-dimension-field">
                                <label>Ширина</label>
                                <input
                                    type="number"
                                    name="width"
                                    value={formData.width}
                                    onChange={handleChange}
                                    step="0.01"
                                    min="0.01"
                                />
                            </div>
                            <div className="create-draft-dimension-field">
                                <label>Высота</label>
                                <input
                                    type="number"
                                    name="height"
                                    value={formData.height}
                                    onChange={handleChange}
                                    step="0.01"
                                    min="0.01"
                                />
                            </div>
                        </div>
                        <div className="create-draft-hint">
                            Заполните все три поля или оставьте все пустыми
                        </div>
                    </div>
                </div>

                {/* Кнопки действий */}
                <div className="create-draft-actions">
                    <button
                        type="submit"
                        className="create-draft-submit-button"
                        disabled={saving}
                    >
                        {saving ? "Сохранение..." : "Сохранить изменения"}
                    </button>
                    <button
                        type="button"
                        className="create-draft-cancel-button"
                        onClick={() => navigate("/waybills")}
                        disabled={saving}
                    >
                        Отмена
                    </button>
                </div>
            </form>
        </div>
    );
}