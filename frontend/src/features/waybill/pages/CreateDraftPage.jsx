import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { createDraft, getPricingRules } from "../api/draftApi";
import "./CreateDraftPage.css";

const ZONE_LABELS = {
    CITY: "Город",
    SUBURBAN: "Пригород",
    INTERCITY: "Межгород",
    REGIONAL: "Регион",
    INTERNATIONAL: "Международная",
};

export default function CreateDraftPage() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [pricingRules, setPricingRules] = useState([]);
    const [rulesLoading, setRulesLoading] = useState(true);
    const [rulesError, setRulesError] = useState(null);

    const [formData, setFormData] = useState({
        recipientPhone: "",
        pricingRuleId: "",
        recipientAddress: "",
    });

    useEffect(() => {
        loadPricingRules();
    }, []);

    const loadPricingRules = async () => {
        try {
            setRulesLoading(true);
            setRulesError(null);
            const rules = await getPricingRules();
            setPricingRules(rules);
        } catch {
            setRulesError("Не удалось загрузить тарифы");
        } finally {
            setRulesLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (!formData.recipientPhone || !formData.pricingRuleId || !formData.recipientAddress) {
            setError("Пожалуйста, заполните все обязательные поля");
            return;
        }

        try {
            setLoading(true);
            await createDraft({
                recipientPhone: formData.recipientPhone,
                recipientAddress: formData.recipientAddress,
                pricingRuleId: Number(formData.pricingRuleId),
            });
            navigate("/waybills");
        } catch (err) {
            setError(err.message || "Ошибка при создании черновика");
        } finally {
            setLoading(false);
        }
    };

    const formatRuleLabel = (rule) => {
        const zone = ZONE_LABELS[rule.deliveryZone] ?? rule.deliveryZone;
        const weight = rule.weightMax
            ? `${rule.weightMin}–${rule.weightMax} кг`
            : `от ${rule.weightMin} кг`;
        return `${rule.ruleName} — ${zone} (${weight})`;
    };

    return (
        <div className="create-draft-container">
            <div className="create-draft-header">
                <h1>Создание черновика накладной</h1>
                <button
                    className="create-draft-back-button"
                    onClick={() => navigate("/waybills")}
                >
                    Назад к списку
                </button>
            </div>

            {error && <div className="error-message">{error}</div>}

            <form className="create-draft-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>
                        Телефон получателя <span className="create-draft-required">*</span>
                    </label>
                    <input
                        type="tel"
                        name="recipientPhone"
                        value={formData.recipientPhone}
                        onChange={handleChange}
                        placeholder="+7XXXXXXXXXX"
                        required
                    />
                </div>

                <div className="form-group">
                    <label>
                        Тариф доставки <span className="create-draft-required">*</span>
                    </label>
                    {rulesError ? (
                        <div className="create-draft-rules-error">
                            {rulesError}
                            <button type="button" className="create-draft-retry-button" onClick={loadPricingRules}>
                                Повторить
                            </button>
                        </div>
                    ) : (
                        <select
                            name="pricingRuleId"
                            value={formData.pricingRuleId}
                            onChange={handleChange}
                            className="create-draft-select"
                            required
                            disabled={rulesLoading}
                        >
                            <option value="">
                                {rulesLoading ? "Загрузка тарифов..." : "Выберите тариф"}
                            </option>
                            {pricingRules.map((rule) => (
                                <option key={rule.id} value={rule.id}>
                                    {formatRuleLabel(rule)}
                                </option>
                            ))}
                        </select>
                    )}
                </div>

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
                    <div className="create-draft-hint">
                        В дальнейшем будет заменено на выбор типа доставки (ПВЗ / Дом)
                    </div>
                </div>

                <div className="create-draft-actions">
                    <button
                        type="submit"
                        className="create-draft-submit-button"
                        disabled={loading || rulesLoading}
                    >
                        {loading ? "Создание..." : "Создать черновик"}
                    </button>
                    <button
                        type="button"
                        className="create-draft-cancel-button"
                        onClick={() => navigate("/waybills")}
                        disabled={loading}
                    >
                        Отмена
                    </button>
                </div>
            </form>
        </div>
    );
}