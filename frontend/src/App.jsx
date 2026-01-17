import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";

import LoginPage from "./features/auth/pages/LoginPage";
import RegisterPage from "./features/auth/pages/RegisterPage";
import ConfirmPhonePage from "./features/auth/pages/ConfirmPhonePage";
import ProfilePage from "./features/user/pages/ProfilePage";

import ProtectedRoute from "./shared/routing/ProtectedRoute";

export default function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/profile" replace />} />

            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/confirm-phone" element={<ConfirmPhonePage />} />

            <Route
                path="/profile"
                element={
                    <ProtectedRoute>
                        <ProfilePage />
                    </ProtectedRoute>
                }
            />

            <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
    );
}
