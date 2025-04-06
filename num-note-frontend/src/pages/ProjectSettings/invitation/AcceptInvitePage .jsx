// src/pages/AcceptInvitationPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { acceptInvite } from "../../../services/api";
import {
    Box,
    Typography,
    CircularProgress,
    Button,
} from "@mui/material";

const AcceptInvitationPage = ({ isUniversal = false }) => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const didRun = useRef(false);
    const [status, setStatus] = useState("loading");

    const token = searchParams.get("token");

    useEffect(() => {
        if (didRun.current || !token) return;
        didRun.current = true;

        const accessToken = localStorage.getItem("token");

        if (!accessToken) {
            sessionStorage.setItem("pendingInviteToken", token);
            sessionStorage.setItem("inviteType", isUniversal ? "universal" : "direct");
            sessionStorage.setItem("postLoginRedirect", window.location.pathname + window.location.search);
            navigate("/auth/login");
            return;
        }

        acceptInvite(token, isUniversal)
            .then(() => setStatus("success"))
            .catch(() => setStatus("error"));
    }, [token]);

    if (status === "loading") {
        return (
            <Box p={4}>
                <CircularProgress />
                <Typography mt={2}>Принимаем приглашение...</Typography>
            </Box>
        );
    }

    if (status === "success") {
        return (
            <Box p={4}>
                <Typography variant="h6" gutterBottom>
                    ✅ Вы успешно присоединились к проекту!
                </Typography>
                <Button onClick={() => navigate("/projects")}>Перейти к проектам</Button>
            </Box>
        );
    }

    if (status === "invalid") {
        return (
            <Box p={4}>
                <Typography color="error">❌ Ссылка не содержит токен. Приглашение недействительно.</Typography>
            </Box>
        );
    }

    return (
        <Box p={4}>
            <Typography color="error">❌ Не удалось принять приглашение. Возможно, оно устарело.</Typography>
            <Button onClick={() => navigate("/")}>На главную</Button>
        </Box>
    );
};

export default AcceptInvitationPage;
