import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";

import LoginPage from "./features/auth/pages/LoginPage";
import RegisterPage from "./features/auth/pages/RegisterPage";
import ConfirmPhonePage from "./features/auth/pages/ConfirmPhonePage";
import ProfilePage from "./features/user/pages/ProfilePage";
import HomePage from "./pages/HomePage";
import WaybillListPage from "./features/waybill/pages/WaybillListPage";
import CreateDraftPage from "./features/waybill/pages/CreateDraftPage";
import EditDraftPage from "./features/waybill/pages/EditDraftPage";

import ProtectedRoute from "./shared/routing/ProtectedRoute";

export default function App() {
    return (
        <Routes>
            <Route path="/" element={<HomePage />} />

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

            <Route
                path="/waybills"
                element={
                    <ProtectedRoute>
                        <WaybillListPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/waybills/create"
                element={
                    <ProtectedRoute>
                        <CreateDraftPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/waybills/edit/:id"
                element={
                    <ProtectedRoute>
                        <EditDraftPage />
                    </ProtectedRoute>
                }
            />

            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    );
}
