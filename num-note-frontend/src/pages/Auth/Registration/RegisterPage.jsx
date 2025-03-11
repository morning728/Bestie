import React, { useContext, useState } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import { ThemeContext } from "../../../ThemeContext";
import Header from "../../../components/Header/Header";
import { registerUser } from '../../../services/api';
import "./RegisterPage.css";

const RegisterPage = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleRegister = async (event) => {
    event.preventDefault();

    try {
      const response = await registerUser(username, password, email, 'USER');
      const { access_token, refresh_token } = response.data;

      // сохраняем токены в localStorage
      localStorage.setItem('token', access_token);
      localStorage.setItem('refreshToken', refresh_token);

      console.log('Регистрация успешна!');
    } catch (error) {
      console.error('Ошибка при регистрации:', error);
    }
  };

  return (
    <Box className={`register-page ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box className="main-register-content">
        <Typography variant="h4" className="register-title">
          Create an Account
        </Typography>
        <form className="register-form">
          <TextField
            label="Username"
            fullWidth
            onChange={(e) => setUsername(e.target.value)}
            margin="normal"
            required
          />
          <TextField
            label="Email"
            fullWidth
            onChange={(e) => setEmail(e.target.value)}
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
            className="register-button"
            onClick={e => handleRegister(e)}
          >
            Register
          </Button>
        </form>
      </Box>
    </Box >
  );
};

export default RegisterPage;