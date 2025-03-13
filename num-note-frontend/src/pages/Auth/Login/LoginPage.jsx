import React, { useContext, useState } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import "./LoginPage.css";
import { ThemeContext } from "../../../ThemeContext";
import { loginUser } from '../../../services/api';
import Header from "../../../components/Header/Header";

const LoginPage = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (event) => {
    event.preventDefault();

    try {
      const response = await loginUser(username, password);

      const { access_token } = response.data;

      // Сохраняем access_token в localStorage
      localStorage.setItem('token', access_token);

      console.log('Логин успешен!');
      window.location.href = '/';
    } catch (error) {
      console.error('Ошибка при авторизации:', error);
    }
  };
  return (
    <Box className={`login-page ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box className="main-login-content">
        <Typography variant="h4" className="login-title">
          Welcome Back
        </Typography>
        <form className="login-form">
          <TextField
            label="Login"
            fullWidth
            onChange={(e) => setUsername(e.target.value)}
            margin="normal"
            required
          />
          <TextField
            label="Password"
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
            onClick={e => { handleLogin(e) }}
          >
            Login
          </Button>
        </form>
      </Box>
    </Box>
  );
};

export default LoginPage;
