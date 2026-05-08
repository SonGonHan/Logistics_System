import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getWaybillById, updateWaybillStatus } from "../api/waybillApi";
import { getRatingByWaybill } from "../../rating/api/ratingApi";
import "./WaybillDetailPage.css";

const STATUS_LABELS = {
    ACCEPTED_AT_PVZ: "Принята в ПВЗ",
    IN_TRANSIT: "В пути",
    AT_SORTING_CENTER: "На сортировке",
    OUT_FOR_DELIVERY: "На доставке",
    READY_FOR_PICKUP: "Готова к выдаче",
    DELIVERED: "Доставлена",
    RETURNING: "Возвращается",
    RETURNED: "Возвращена",
    CANCELLED: "Отменена",
    LOST: "Утеряна",
};

const NEXT_STATUSES = {
    ACCEPTED_AT_PVZ: ["IN_TRANSIT", "CANCELLED"],
    IN_TRANSIT: ["AT_SORTING_CENTER", "OUT_FOR_DELIVERY", "RETURNING"],
    AT_SORTING_CENTER: ["IN_TRANSIT", "OUT_FOR_DELIVERY"],
    OUT_FOR_DELIVERY: ["READY_FOR_PICKUP", "DELIVERED", "RETURNING"],
    READY_FOR_PICKUP: ["DELIVERED", "RETURNING"],
};

export default function WaybillDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [waybill, setWaybill] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const [existingRating, setExistingRating] = useState(null);

    useEffect(() => {
        loadWaybill();
    }, [id]);

    const availableNextStatuses = useMemo(() => {
        if (!waybill) {
            return [];
        }
        return NEXT_STATUSES[waybill.status] || [];
    }, [waybill]);

    const loadWaybill = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getWaybillById(id);
            setWaybill(data);
            setExistingRating(null);

            if (data.status === "DELIVERED") {
                try {
                    const rating = await getRatingByWaybill(id);
                    setExistingRating(rating);
                } catch (err) {
                    if (err.status !== 404) console.error(err);
                }
            }
        } catch (err) {
            setError(err.message || "Ошибка загрузки накладной");
        } finally {
            setLoading(false);
        }
    };

    const handleStatusUpdate = async (newStatus) => {
        try {
            setSaving(true);
            setError(null);
            const updated = await updateWaybillStatus(id, {
                newStatus,
                notes: `Статус изменен на ${STATUS_LABELS[newStatus] || newStatus}`,
            });
            setWaybill(updated);
        } catch (err) {
            setError(err.message || "Ошибка обновления статуса");
        } finally {
            setSaving(false);
        }
    };

    const formatDate = (value) => {
        if (!value) {
            return "—";
        }
        return new Date(value).toLocaleString("ru-RU");
    };

    const formatPrice = (value) => {
        if (value == null) {
            return "—";
        }
        return `${Number(value).toFixed(2)} ₽`;
    };

    const getStatusClass = (status) => `waybill-status-${String(status || "").toLowerCase()}`;

    if (loading) {
        return <div className="waybill-detail-container">Загрузка...</div>;
    }

    if (error && !waybill) {
        return (
            <div className="waybill-detail-container">
                <div className="error-message">{error}</div>
                <button onClick={() => navigate("/waybills")}>Назад к списку</button>
            </div>
        );
    }

    return (
        <div className="waybill-detail-container">
            <div className="waybill-detail-header">
                <button className="waybill-back-button" onClick={() => navigate("/waybills")}>
                    Назад
                </button>
                <div>
                    <h1>{waybill.waybillNumber}</h1>
                    <span className={`waybill-status-badge ${getStatusClass(waybill.status)}`}>
                        {STATUS_LABELS[waybill.status] || waybill.status}
                    </span>
                </div>
            </div>

            {error ? <div className="error-message">{error}</div> : null}

            <div className="waybill-detail-grid">
                <section className="waybill-detail-card">
                    <h2>Основная информация</h2>
                    <div className="waybill-info-row"><span>ID:</span><strong>{waybill.id}</strong></div>
                    <div className="waybill-info-row"><span>Отправитель:</span><strong>{waybill.senderUserId}</strong></div>
                    <div className="waybill-info-row"><span>Получатель:</span><strong>{waybill.recipientUserId}</strong></div>
                    <div className="waybill-info-row"><span>Адрес:</span><strong>{waybill.recipientAddress}</strong></div>
                    <div className="waybill-info-row"><span>Тариф:</span><strong>{waybill.pricingRuleId}</strong></div>
                    <div className="waybill-info-row"><span>Цена:</span><strong>{formatPrice(waybill.finalPrice)}</strong></div>
                </section>

                <section className="waybill-detail-card">
                    <h2>Даты и статус</h2>
                    <div className="waybill-info-row"><span>Создана:</span><strong>{formatDate(waybill.createdAt)}</strong></div>
                    <div className="waybill-info-row"><span>Принята:</span><strong>{formatDate(waybill.acceptedAt)}</strong></div>
                    <div className="waybill-info-row"><span>Создатель:</span><strong>{waybill.waybillCreatorId}</strong></div>
                    <div className="waybill-info-row"><span>Текущий статус:</span><strong>{STATUS_LABELS[waybill.status] || waybill.status}</strong></div>
                </section>
            </div>

            <section className="waybill-detail-card">
                <h2>Доступные следующие статусы</h2>
                {availableNextStatuses.length === 0 ? (
                    <p className="waybill-muted">Для текущего статуса дальнейшие переходы недоступны.</p>
                ) : (
                    <div className="waybill-status-actions">
                        {availableNextStatuses.map((status) => (
                            <button
                                key={status}
                                className="waybill-next-status-button"
                                disabled={saving}
                                onClick={() => handleStatusUpdate(status)}
                            >
                                {saving ? "Сохранение..." : STATUS_LABELS[status] || status}
                            </button>
                        ))}
                    </div>
                )}
            </section>

            {waybill.status === "DELIVERED" && (
                <section className="waybill-detail-card">
                    <h2>Оценка доставки</h2>
                    {existingRating ? (
                        <div>
                            <div className="waybill-info-row">
                                <span>Оценка:</span>
                                <strong>{"★".repeat(existingRating.score)}{"☆".repeat(5 - existingRating.score)} ({existingRating.score}/5)</strong>
                            </div>
                            {existingRating.comment && (
                                <div className="waybill-info-row">
                                    <span>Отзыв:</span>
                                    <strong>{existingRating.comment}</strong>
                                </div>
                            )}
                        </div>
                    ) : (
                        <div>
                            <p className="waybill-muted">Оценка ещё не оставлена</p>
                            <button
                                className="waybill-next-status-button"
                                onClick={() => navigate(`/ratings/waybill/${waybill.id}`)}
                            >
                                Оставить оценку
                            </button>
                        </div>
                    )}
                </section>
            )}
        </div>
    );
}
