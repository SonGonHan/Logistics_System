import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { deleteDraft, getUserDrafts } from "../api/draftApi";
import { getUserWaybills } from "../api/waybillApi";
import "./WaybillListPage.css";

const STATUS_LABELS = {
    PENDING: "Ожидает",
    CONFIRMED: "Подтвержден",
    CANCELLED: "Отменен",
    ACCEPTED_AT_PVZ: "Принята в ПВЗ",
    IN_TRANSIT: "В пути",
    AT_SORTING_CENTER: "На сортировке",
    OUT_FOR_DELIVERY: "На доставке",
    READY_FOR_PICKUP: "Готова к выдаче",
    DELIVERED: "Доставлена",
    RETURNING: "Возвращается",
    RETURNED: "Возвращена",
    LOST: "Утеряна",
};

const STATUS_CLASSES = {
    PENDING: "waybill-status-pending",
    CONFIRMED: "waybill-status-confirmed",
    CANCELLED: "waybill-status-cancelled",
    ACCEPTED_AT_PVZ: "waybill-status-waybill-accepted",
    IN_TRANSIT: "waybill-status-waybill-transit",
    AT_SORTING_CENTER: "waybill-status-waybill-transit",
    OUT_FOR_DELIVERY: "waybill-status-waybill-transit",
    READY_FOR_PICKUP: "waybill-status-waybill-pickup",
    DELIVERED: "waybill-status-waybill-delivered",
    RETURNING: "waybill-status-waybill-returning",
    RETURNED: "waybill-status-waybill-delivered",
    LOST: "waybill-status-waybill-cancelled",
};

export default function WaybillListPage() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState("drafts");
    const [drafts, setDrafts] = useState([]);
    const [waybills, setWaybills] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [filterStatus, setFilterStatus] = useState("");
    const [sortBy, setSortBy] = useState("createdAt");

    useEffect(() => {
        if (activeTab === "drafts") {
            loadDrafts();
            return;
        }
        loadWaybills();
    }, [activeTab]);

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

    const loadWaybills = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await getUserWaybills();
            setWaybills(data);
        } catch (err) {
            setError(err.message || "Ошибка загрузки накладных");
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
        if (!dateString) {
            return "—";
        }
        return new Date(dateString).toLocaleString("ru-RU");
    };

    const formatPrice = (price) => {
        if (price == null) {
            return "—";
        }
        return `${Number(price).toFixed(2)} ₽`;
    };

    const renderStatus = (status) => (
        <span className={`waybill-status-badge ${STATUS_CLASSES[status] || ""}`}>
            {STATUS_LABELS[status] || status}
        </span>
    );

    if (loading) {
        return <div className="waybill-list-container">Загрузка...</div>;
    }

    if (error) {
        return (
            <div className="waybill-list-container">
                <div className="error-message">{error}</div>
                <button onClick={activeTab === "drafts" ? loadDrafts : loadWaybills}>
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

            <div className="waybill-tabs">
                <button
                    className={`waybill-tab-button ${activeTab === "drafts" ? "active" : ""}`}
                    onClick={() => setActiveTab("drafts")}
                >
                    Черновики
                </button>
                <button
                    className={`waybill-tab-button ${activeTab === "waybills" ? "active" : ""}`}
                    onClick={() => setActiveTab("waybills")}
                >
                    Накладные
                </button>
            </div>

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
                    </select>
                </div>

                <div className="waybill-filter-placeholder">
                    (Фильтры и сортировка пока не реализованы)
                </div>
            </div>

            {activeTab === "drafts" && drafts.length === 0 ? (
                <div className="waybill-empty-state">
                    <p>У вас пока нет черновиков</p>
                </div>
            ) : null}

            {activeTab === "drafts" && drafts.length > 0 ? (
                <div className="waybill-table-container">
                    <table className="waybill-table">
                        <thead>
                            <tr>
                                <th>Штрих-код</th>
                                <th>Адрес доставки</th>
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
                                    <td className="waybill-address-cell">{draft.recipientAddress}</td>
                                    <td>{formatPrice(draft.estimatedPrice)}</td>
                                    <td>{renderStatus(draft.draftStatus)}</td>
                                    <td>{formatDate(draft.createdAt)}</td>
                                    <td>
                                        <div className="waybill-actions">
                                            {draft.draftStatus === "PENDING" ? (
                                                <>
                                                    <button
                                                        className="waybill-edit-button"
                                                        onClick={() => navigate(`/waybills/edit/${draft.id}`)}
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
                                            ) : null}
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : null}

            {activeTab === "waybills" && waybills.length === 0 ? (
                <div className="waybill-empty-state">
                    <p>У вас пока нет подтвержденных накладных</p>
                </div>
            ) : null}

            {activeTab === "waybills" && waybills.length > 0 ? (
                <div className="waybill-table-container">
                    <table className="waybill-table">
                        <thead>
                            <tr>
                                <th>Номер накладной</th>
                                <th>Статус</th>
                                <th>Адрес</th>
                                <th>Цена</th>
                                <th>Дата создания</th>
                            </tr>
                        </thead>
                        <tbody>
                            {waybills.map((waybill) => (
                                <tr
                                    key={waybill.id}
                                    className="waybill-row-clickable"
                                    onClick={() => navigate(`/waybills/detail/${waybill.id}`)}
                                >
                                    <td>{waybill.waybillNumber}</td>
                                    <td>{renderStatus(waybill.status)}</td>
                                    <td className="waybill-address-cell">{waybill.recipientAddress}</td>
                                    <td>{formatPrice(waybill.finalPrice)}</td>
                                    <td>{formatDate(waybill.createdAt)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : null}
        </div>
    );
}
