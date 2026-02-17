import React from "react";
import { useNavigate } from "react-router-dom";

export default function HomePage() {
    const navigate = useNavigate();

    return (
        <div style={styles.container}>
            <h1 style={styles.title}>Добро пожаловать в систему логистики</h1>
            <div style={styles.buttonContainer}>
                <button
                    style={styles.button}
                    onClick={() => navigate("/login")}
                >
                    Авторизация
                </button>
                <button
                    style={styles.button}
                    onClick={() => navigate("/waybills")}
                >
                    Накладные и черновики
                </button>
            </div>
        </div>
    );
}

const styles = {
    container: {
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "100vh",
        padding: "20px",
    },
    title: {
        fontSize: "32px",
        marginBottom: "40px",
        textAlign: "center",
    },
    buttonContainer: {
        display: "flex",
        gap: "20px",
        flexWrap: "wrap",
        justifyContent: "center",
    },
    button: {
        padding: "15px 30px",
        fontSize: "18px",
        backgroundColor: "#007bff",
        color: "white",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
        minWidth: "200px",
    },
};