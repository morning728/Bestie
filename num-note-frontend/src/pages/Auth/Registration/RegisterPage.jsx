import React, { useContext, useState } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import { ThemeContext } from "../../../ThemeContext";
import Header from "../../../components/Header/Header";
import { registerUser } from '../../../services/api';
import "./RegisterPage.css";
import { useTranslation } from "react-i18next";

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
        <Typography variant="h4" className="register-title">
          {t("register_title")}
        </Typography>
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
          {error && <Typography color="error" mt={2}>{error}</Typography>}
        </form>
      </Box>
    </Box>
  );
};

export default RegisterPage;
