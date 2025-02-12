import React, { useContext, useState } from "react";
import { Box, TextField, Button, Typography } from "@mui/material";
import { ThemeContext } from "../../../ThemeContext";
import Header from "../../../components/Header/Header";
import "./RegisterPage.css";

const RegisterPage = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);
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
            margin="normal"
            required
          />
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
            className="register-button"
          >
            Register
          </Button>
        </form>
      </Box>
    </Box >
  );
};

export default RegisterPage;