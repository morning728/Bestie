import React, { useContext, useState } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import "./LoginPage.css";
import { ThemeContext } from "../../../ThemeContext";
import Header from "../../../components/Header/Header";

const LoginPage = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);
  return (
    <Box className={`login-page ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box className="main-login-content">
        <Typography variant="h4" className="login-title">
          Welcome Back
        </Typography>
        <form className="login-form">
          <TextField
            label="Email"
            fullWidth
            margin="normal"
            required
          />
          <TextField
            label="Password"
            type="password"
            fullWidth
            margin="normal"
            required
          />
          <Button
            variant="contained"
            color="primary"
            size="large"
            fullWidth
            className="login-button"
          >
            Login
          </Button>
        </form>
      </Box>
    </Box>
  );
};

export default LoginPage;
