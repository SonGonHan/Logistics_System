import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { completeAcceptance, startAcceptance } from "../api/acceptanceApi";
import "./AcceptancePage.css";

const FACILITIES = [
    { id: 1, name: "ПВЗ #1 - Москва, ул. Ленина, 10" },
    { id: 2, name: "ПВЗ #2 - Москва, ул. Пушкина, 25" },
    { id: 3, name: "ПВЗ #3 - Санкт-Петербург, Невский пр., 50" },
];

export default function AcceptancePage() {
    const navigate = useNavigate();
    const [step, setStep] = useState("scan");
    const [barcode, setBarcode] = useState("");
    const [facilityId, setFacilityId] = useState("");
    const [draftData, setDraftData] = useState(null);
    const [waybillData, setWaybillData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleStartAcceptance = async (e) => {
        e.preventDefault();
        setError(null);

        if (!barcode.trim()) {
            setError("Введите штрих-код");
            return;
        }

        if (!facilityId) {
            setError("Выберите пункт приёмки");
            return;
        }

        try {
            setLoading(true);
            const data = await startAcceptance({
                barcode: barcode.trim(),
                facilityId: Number(facilityId),
            });
            setDraftData(data);
            setStep("review");
        } catch (err) {
            setError(err.message || "Ошибка при поиске черновика");
        } finally {
            setLoading(false);
        }
    };

    const handleCompleteAcceptance = async () => {
        setError(null);

        try {
            setLoading(true);
            const data = await completeAcceptance(draftData.draftId, {
                draftId: draftData.draftId,
                facilityId: Number(facilityId),
            });
            setWaybillData(data);
            setStep("done");
        } catch (err) {
            setError(err.message || "Ошибка при завершении приёмки");
        } finally {
            setLoading(false);
        }
    };

    const handleReset = () => {
        setStep("scan");
        setBarcode("");
        setDraftData(null);
        setWaybillData(null);
        setError(null);
    };

    const handleBackToScan = () => {
        setStep("scan");
        setDraftData(null);
        setError(null);
    };

    const formatPrice = (price) => {
        if (price == null) return "—";
        return `${Number(price).toFixed(2)} ₽`;
    };

    const formatDate = (dateString) => {
        if (!dateString) return "—";
        return new Date(dateString).toLocaleString("ru-RU");
    };

    return (
        <div className="acceptance-container">
            <div className="acceptance-header">
                <h1>Приёмка посылки на ПВЗ</h1>
                <button
                    className="acceptance-back-button"
                    onClick={() => navigate("/waybills")}
                >
                    Назад к списку
                </button>
            </div>

            <div className="acceptance-steps">
                <div className={`acceptance-step ${step === "scan" ? "acceptance-step-active" : ""} ${step !== "scan" ? "acceptance-step-done" : ""}`}>
                    1. Сканирование
                </div>
                <div className="acceptance-step-divider">→</div>
                <div className={`acceptance-step ${step === "review" ? "acceptance-step-active" : ""} ${step === "done" ? "acceptance-step-done" : ""}`}>
                    2. Проверка
                </div>
                <div className="acceptance-step-divider">→</div>
                <div className={`acceptance-step ${step === "done" ? "acceptance-step-active" : ""}`}>
                    3. Готово
                </div>
            </div>

            {error && <div className="error-message">{error}</div>}

            {step === "scan" && (
                <form className="acceptance-form" onSubmit={handleStartAcceptance}>
                    <div className="form-group">
                        <label>
                            Штрих-код посылки <span className="acceptance-required">*</span>
                        </label>
                        <input
                            type="text"
                            value={barcode}
                            onChange={(e) => setBarcode(e.target.value)}
                            placeholder="Отсканируйте или введите штрих-код (напр. DRF-260209-123456)"
                            autoFocus
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group">
                        <label>
                            Пункт приёмки (ПВЗ) <span className="acceptance-required">*</span>
                        </label>
                        <select
                            value={facilityId}
                            onChange={(e) => setFacilityId(e.target.value)}
                            className="acceptance-select"
                            required
                            disabled={loading}
                        >
                            <option value="">Выберите ПВЗ</option>
                            {FACILITIES.map((facility) => (
                                <option key={facility.id} value={facility.id}>
                                    {facility.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="acceptance-actions">
                        <button
                            type="submit"
                            className="acceptance-submit-button"
                            disabled={loading}
                        >
                            {loading ? "Поиск..." : "Начать приёмку"}
                        </button>
                    </div>
                </form>
            )}

            {step === "review" && draftData && (
                <div className="acceptance-review">
                    <h2>Данные черновика</h2>

                    <div className="acceptance-details">
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Штрих-код:</span>
                            <span className="acceptance-detail-value">{draftData.barcode}</span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">ID отправителя:</span>
                            <span className="acceptance-detail-value">{draftData.senderUserId}</span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">ID получателя:</span>
                            <span className="acceptance-detail-value">{draftData.recipientUserId}</span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Адрес доставки:</span>
                            <span className="acceptance-detail-value">{draftData.recipientAddress}</span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Предварительная стоимость:</span>
                            <span className="acceptance-detail-value acceptance-price">
                                {formatPrice(draftData.estimatedPrice)}
                            </span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Статус черновика:</span>
                            <span className="acceptance-detail-value">
                                <span className="acceptance-status-badge acceptance-status-pending">
                                    {draftData.draftStatus}
                                </span>
                            </span>
                        </div>
                    </div>

                    <div className="acceptance-actions">
                        <button
                            className="acceptance-confirm-button"
                            onClick={handleCompleteAcceptance}
                            disabled={loading}
                        >
                            {loading ? "Оформление..." : "Завершить приёмку и создать накладную"}
                        </button>
                        <button
                            className="acceptance-cancel-button"
                            onClick={handleBackToScan}
                            disabled={loading}
                        >
                            Назад
                        </button>
                    </div>
                </div>
            )}

            {step === "done" && waybillData && (
                <div className="acceptance-done">
                    <div className="acceptance-success-icon">✓</div>
                    <h2>Приёмка завершена</h2>
                    <p className="acceptance-success-message">
                        Накладная успешно создана
                    </p>

                    <div className="acceptance-details">
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Номер накладной:</span>
                            <span className="acceptance-detail-value acceptance-waybill-number">
                                {waybillData.waybillNumber}
                            </span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Итоговая цена:</span>
                            <span className="acceptance-detail-value acceptance-price">
                                {formatPrice(waybillData.finalPrice)}
                            </span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Статус:</span>
                            <span className="acceptance-detail-value">
                                <span className="acceptance-status-badge acceptance-status-accepted">
                                    {waybillData.waybillStatus}
                                </span>
                            </span>
                        </div>
                        <div className="acceptance-detail-row">
                            <span className="acceptance-detail-label">Дата приёмки:</span>
                            <span className="acceptance-detail-value">
                                {formatDate(waybillData.acceptedAt)}
                            </span>
                        </div>
                    </div>

                    <div className="acceptance-actions">
                        <button
                            className="acceptance-submit-button"
                            onClick={handleReset}
                        >
                            Новая приёмка
                        </button>
                        <button
                            className="acceptance-back-button"
                            onClick={() => navigate("/waybills")}
                        >
                            К списку накладных
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
