// src/pages/AcceptInvitationPage.jsx
import React, { useEffect, useState, useRef, useContext } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { acceptInvite } from "../../../services/api";
import { ThemeContext } from "../../../ThemeContext";
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
    const { darkMode } = useContext(ThemeContext);

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

      useEffect(() => {
        if (status === "success") {
          const timer = setTimeout(() => {
            navigate("/projects");
          }, 3000);
          return () => clearTimeout(timer);
        }
      }, [status, navigate]);

    if (status === "loading") {
        return (
            <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
                <CircularProgress />
                <Typography mt={2} className={`main-title ${darkMode ? "night" : "day"}`}>Принимаем приглашение...</Typography>
            </Box>
        );
    }

    if (status === "success") {
        return (
            <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
                <Typography variant="h6" gutterBottom className={`main-title ${darkMode ? "night" : "day"}`}>
                    ✅ Вы успешно присоединились к проекту! Вы будете перенаправлены к проектам через несколько секунд...
                </Typography>
                <Button onClick={() => navigate("/projects")}>Перейти к проектам</Button>
            </Box>
        );
    }

    if (status === "invalid") {
        return (
            <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
                <Typography color="error">❌ Ссылка не содержит токен. Приглашение недействительно.</Typography>
            </Box>
        );
    }

    return (
        <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
            <Typography color="error">❌ Не удалось принять приглашение. Возможно, оно устарело.</Typography>
            <Button onClick={() => navigate("/")}>На главную</Button>
        </Box>
    );
};

export default AcceptInvitationPage;
