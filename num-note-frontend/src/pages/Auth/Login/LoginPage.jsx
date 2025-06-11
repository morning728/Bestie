import React, { useContext, useState, useEffect } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import { useTranslation } from "react-i18next";
import "./LoginPage.css";
import { Paper } from "@mui/material";
import { ThemeContext } from "../../../ThemeContext";
import { loginUser, acceptInvite } from "../../../services/api";
import Header from "../../../components/Header/Header";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom"; // добавь импорт

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
        <Typography variant="h4" className={`main-title ${darkMode ? "night" : "day"}`}>
          {t("login_welcome_back")}
        </Typography>
        <Paper
          elevation={8}
          sx={{
            background: darkMode
              ? "linear-gradient(300deg, #1c1c3c, #2b2b60)"
              : "linear-gradient(to top left, #d16ba5, #c777b9, #ba83ca, #aa8fd8, #9a9ae1, #8aa7ec, #79b3f4, #69bff8);",
            color: darkMode ? "#00f6ff" : "#d81b60",
            boxShadow: darkMode
              ? "0 0 3px #00f6ff, 0 0 24px #00f6ff"
              : "0 0 3px #ff90e8, 0 0 24px #ff90e8",
            borderRadius: 3,
            px: 4,
            py: 3,
            opacity: darkMode ? "0.93" : "0.8",
            maxWidth: 400,
            margin: "0 auto"
          }}
        >
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
            <Typography variant="body2" align="center" mt={2}>
              {t("dont_have_account_answer")}{" "}
              <Link to="/auth/register" style={{ color: darkMode ? "#00f6ff" : "#d81b60", textDecoration: "underline" }}>
                {t("register_button")}
              </Link>
            </Typography>
          </form>
        </Paper>
      </Box>
    </Box>
  );
};

export default LoginPage;
