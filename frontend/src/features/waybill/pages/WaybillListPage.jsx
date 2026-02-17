import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUserDrafts, deleteDraft } from "../api/draftApi";
import "./WaybillListPage.css";

export default function WaybillListPage() {
    const navigate = useNavigate();
    const [drafts, setDrafts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Заглушки для фильтров и сортировки
    const [filterStatus, setFilterStatus] = useState("");
    const [sortBy, setSortBy] = useState("createdAt");

    useEffect(() => {
        loadDrafts();
    }, []);

    const loadDrafts = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getUserDrafts();
            setDrafts(data);
        } catch (err) {
            setError(err.message || "Ошибка загрузки черновиков");
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Вы уверены, что хотите удалить этот черновик?")) {
            return;
        }

        try {
            await deleteDraft(id);
            await loadDrafts();
        } catch (err) {
            alert(err.message || "Ошибка удаления черновика");
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return "—";
        return new Date(dateString).toLocaleString("ru-RU");
    };

    const formatPrice = (price) => {
        if (!price) return "—";
        return `${price.toFixed(2)} ₽`;
    };

    const getStatusText = (status) => {
        const statusMap = {
            PENDING: "Ожидает",
            CONFIRMED: "Подтвержден",
            CANCELLED: "Отменен",
        };
        return statusMap[status] || status;
    };

    const getStatusClass = (status) => {
        const classMap = {
            PENDING: "waybill-status-pending",
            CONFIRMED: "waybill-status-confirmed",
            CANCELLED: "waybill-status-cancelled",
        };
        return classMap[status] || "";
    };

    if (loading) {
        return <div className="waybill-list-container">Загрузка...</div>;
    }

    if (error) {
        return (
            <div className="waybill-list-container">
                <div className="error-message">{error}</div>
                <button onClick={loadDrafts}>
                    Повторить
                </button>
            </div>
        );
    }

    return (
        <div className="waybill-list-container">
            <div className="waybill-list-header">
                <h1>Накладные и черновики</h1>
                <button
                    className="waybill-create-button"
                    onClick={() => navigate("/waybills/create")}
                >
                    + Создать черновик
                </button>
            </div>

            {/* Заглушки для фильтров и сортировки */}
            <div className="waybill-filters">
                <div className="waybill-filter-group">
                    <label>Фильтр по статусу:</label>
                    <select
                        className="waybill-filter-select"
                        value={filterStatus}
                        onChange={(e) => setFilterStatus(e.target.value)}
                        disabled
                    >
                        <option value="">Все</option>
                        <option value="PENDING">Ожидает</option>
                        <option value="CONFIRMED">Подтвержден</option>
                        <option value="CANCELLED">Отменен</option>
                    </select>
                </div>

                <div className="waybill-filter-group">
                    <label>Сортировка:</label>
                    <select
                        className="waybill-filter-select"
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value)}
                        disabled
                    >
                        <option value="createdAt">По дате создания</option>
                        <option value="price">По цене</option>
                        <option value="weight">По весу</option>
                    </select>
                </div>

                <div className="waybill-filter-placeholder">
                    (Фильтры и сортировка пока не реализованы)
                </div>
            </div>

            {/* Список черновиков */}
            {drafts.length === 0 ? (
                <div className="waybill-empty-state">
                    <p>У вас пока нет черновиков</p>
                </div>
            ) : (
                <div className="waybill-table-container">
                    <table className="waybill-table">
                        <thead>
                            <tr>
                                <th>Штрих-код</th>
                                <th>Адрес доставки</th>
                                <th>Вес (кг)</th>
                                <th>Цена</th>
                                <th>Статус</th>
                                <th>Дата создания</th>
                                <th>Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            {drafts.map((draft) => (
                                <tr key={draft.id}>
                                    <td>{draft.barcode}</td>
                                    <td className="waybill-address-cell">
                                        {draft.recipientAddress}
                                    </td>
                                    <td>{draft.weightDeclared}</td>
                                    <td>{formatPrice(draft.estimatedPrice)}</td>
                                    <td>
                                        <span
                                            className={`waybill-status-badge ${getStatusClass(
                                                draft.draftStatus
                                            )}`}
                                        >
                                            {getStatusText(draft.draftStatus)}
                                        </span>
                                    </td>
                                    <td>{formatDate(draft.createdAt)}</td>
                                    <td>
                                        <div className="waybill-actions">
                                            {draft.draftStatus === "PENDING" && (
                                                <>
                                                    <button
                                                        className="waybill-edit-button"
                                                        onClick={() =>
                                                            navigate(`/waybills/edit/${draft.id}`)
                                                        }
                                                    >
                                                        Редактировать
                                                    </button>
                                                    <button
                                                        className="waybill-delete-button"
                                                        onClick={() => handleDelete(draft.id)}
                                                    >
                                                        Удалить
                                                    </button>
                                                </>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}