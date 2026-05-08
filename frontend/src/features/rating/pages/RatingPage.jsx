import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getRatingByWaybill, submitRating } from "../api/ratingApi";
import { getWaybillById } from "../../waybill/api/waybillApi";
import "./RatingPage.css";

export default function RatingPage() {
    const { waybillId } = useParams();
    const navigate = useNavigate();

    const [waybill, setWaybill] = useState(null);
    const [existingRating, setExistingRating] = useState(null);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    const [score, setScore] = useState(0);
    const [comment, setComment] = useState("");

    useEffect(() => {
        loadData();
    }, [waybillId]);

    const loadData = async () => {
        try {
            setLoading(true);
            setError(null);

            const wb = await getWaybillById(waybillId);
            setWaybill(wb);

            try {
                const rating = await getRatingByWaybill(waybillId);
                setExistingRating(rating);
            } catch (err) {
                if (err.status !== 404) throw err;
            }
        } catch (err) {
            setError(err.message || "Ошибка загрузки данных");
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        if (score < 1 || score > 5) {
            setError("Выберите оценку от 1 до 5");
            return;
        }

        try {
            setSubmitting(true);
            const rating = await submitRating({
                waybillId: Number(waybillId),
                score,
                comment: comment.trim() || null,
            });
            setExistingRating(rating);
            setSuccess(true);
        } catch (err) {
            setError(err.message || "Ошибка при отправке оценки");
        } finally {
            setSubmitting(false);
        }
    };

    const renderStars = (value, interactive = false) => {
        return (
            <div className="rating-stars">
                {[1, 2, 3, 4, 5].map((star) => (
                    <span
                        key={star}
                        className={`rating-star ${star <= value ? "rating-star-filled" : "rating-star-empty"} ${interactive ? "rating-star-interactive" : ""}`}
                        onClick={interactive ? () => setScore(star) : undefined}
                    >
                        ★
                    </span>
                ))}
            </div>
        );
    };

    if (loading) {
        return <div className="rating-container">Загрузка...</div>;
    }

    if (error && !waybill) {
        return (
            <div className="rating-container">
                <div className="error-message">{error}</div>
                <button onClick={() => navigate(-1)}>Назад</button>
            </div>
        );
    }

    return (
        <div className="rating-container">
            <div className="rating-header">
                <h1>Оценка доставки</h1>
                <button
                    className="rating-back-button"
                    onClick={() => navigate(`/waybills/detail/${waybillId}`)}
                >
                    Назад к накладной
                </button>
            </div>

            <div className="rating-waybill-info">
                <span>Накладная: <strong>{waybill.waybillNumber}</strong></span>
                <span>Адрес: <strong>{waybill.recipientAddress}</strong></span>
            </div>

            {error && <div className="error-message">{error}</div>}

            {existingRating ? (
                <div className="rating-existing">
                    {success && (
                        <div className="rating-success-message">
                            Спасибо! Ваша оценка сохранена.
                        </div>
                    )}
                    <h2>Ваша оценка</h2>
                    {renderStars(existingRating.score)}
                    <p className="rating-score-text">{existingRating.score} из 5</p>
                    {existingRating.comment && (
                        <div className="rating-comment-display">
                            <strong>Отзыв:</strong>
                            <p>{existingRating.comment}</p>
                        </div>
                    )}
                    <p className="rating-date">
                        Оценка оставлена: {new Date(existingRating.createdAt).toLocaleString("ru-RU")}
                    </p>
                </div>
            ) : (
                <form className="rating-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Оценка <span className="rating-required">*</span></label>
                        {renderStars(score, true)}
                        {score > 0 && <span className="rating-score-label">{score} из 5</span>}
                    </div>

                    <div className="form-group">
                        <label>Отзыв (необязательно)</label>
                        <textarea
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            placeholder="Расскажите о вашем опыте доставки..."
                            rows={4}
                            disabled={submitting}
                        />
                    </div>

                    <div className="rating-actions">
                        <button
                            type="submit"
                            className="rating-submit-button"
                            disabled={submitting || score === 0}
                        >
                            {submitting ? "Отправка..." : "Отправить оценку"}
                        </button>
                        <button
                            type="button"
                            className="rating-cancel-button"
                            onClick={() => navigate(`/waybills/detail/${waybillId}`)}
                            disabled={submitting}
                        >
                            Отмена
                        </button>
                    </div>
                </form>
            )}
        </div>
    );
}
