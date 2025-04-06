import React, { useContext, useState, useEffect } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import "./LoginPage.css";
import { ThemeContext } from "../../../ThemeContext";
import { loginUser, acceptInvite } from "../../../services/api";
import Header from "../../../components/Header/Header";
import { useNavigate } from "react-router-dom";

const LoginPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (event) => {
    event.preventDefault();

    try {
      const response = await loginUser(username, password);
      const { access_token } = response.data;

      // Сохраняем access_token в localStorage
      localStorage.setItem('token', access_token);

      // Проверяем, есть ли приглашение
      const token = sessionStorage.getItem("pendingInviteToken");
      const inviteType = sessionStorage.getItem("inviteType");
      const redirect = sessionStorage.getItem("postLoginRedirect");

      if (token) {
        // Принимаем приглашение
        sessionStorage.removeItem("pendingInviteToken");
        sessionStorage.removeItem("inviteType");
        sessionStorage.removeItem("postLoginRedirect");

        try {
          await acceptInvite(token, inviteType === "universal");
          navigate("/projects"); // или конкретный проект, если знаешь projectId
        } catch (e) {
          console.error("Ошибка при принятии приглашения:", e);
          navigate("/accept-error");
        }
      } else if (redirect) {
        sessionStorage.removeItem("postLoginRedirect");
        navigate(redirect);
      } else {
        navigate("/"); // обычный вход
      }

    } catch (error) {
      console.error("Ошибка при авторизации:", error);
      // Можно добавить отображение ошибки в UI
    }
  };

  return (
    <Box className={`login-page ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box className="main-login-content">
        <Typography variant="h4" className="login-title">
          {t("login_welcome_back")}
        </Typography>
        <form className="login-form">
          <TextField
            label={t("login_username")}
            fullWidth
            onChange={(e) => setUsername(e.target.value)}
            margin="normal"
            required
          />
          <TextField
            label={t("login_password")}
            type="password"
            fullWidth
            onChange={(e) => setPassword(e.target.value)}
            margin="normal"
            required
          />
          <Button
            variant="contained"
            color="primary"
            size="large"
            fullWidth
            className="login-button"
            onClick={handleLogin}
          >
            {t("login_button")}
          </Button>
        </form>
      </Box>
    </Box>
  );
};

export default LoginPage;
