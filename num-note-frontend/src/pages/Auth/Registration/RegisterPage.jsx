import React, { useContext, useState } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import { ThemeContext } from "../../../ThemeContext";
import Header from "../../../components/Header/Header";
import { registerUser } from '../../../services/api';
import "./RegisterPage.css";
import { Paper } from "@mui/material";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom"; // добавь импорт

const RegisterPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleRegister = async (event) => {
    event.preventDefault();
    setError("");

    try {
      const response = await registerUser(username, password, email, 'USER');
      const { access_token } = response.data;
      localStorage.setItem('token', access_token);
      window.location.href = '/';
    } catch (error) {
      console.error('Ошибка при регистрации:', error);
      setError(t("register_error"));
    }
  };

  return (
    <Box className={`register-page ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box className="main-register-content">
        <Typography variant="h4" className={`main-title ${darkMode ? "night" : "day"}`} >
          {t("register_title")}
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
          <form className="register-form" onSubmit={handleRegister}>
            <TextField
              label={t("register_username")}
              fullWidth
              onChange={(e) => setUsername(e.target.value)}
              margin="normal"
              required
              autoFocus
            />
            <TextField
              label={t("register_email")}
              fullWidth
              onChange={(e) => setEmail(e.target.value)}
              margin="normal"
              required
            />
            <TextField
              label={t("register_password")}
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
              className="register-button"
              type="submit"
            >
              {t("register_button")}
            </Button>
            {/* Ссылка на логин */}
            <Typography variant="body2" align="center" mt={2}>
              {t("have_account_answer")}{" "}
              <Link to="/auth/login" style={{ color: darkMode ? "#00f6ff" : "#d81b60", textDecoration: "underline" }}>
                {t("login_button")}
              </Link>
            </Typography>
            {error && <Typography color="error" mt={2}>{error}</Typography>}
          </form>
        </Paper>
      </Box>
    </Box>
  );
};

export default RegisterPage;
