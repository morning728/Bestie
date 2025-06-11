import React, { useEffect, useState, useRef, useContext } from "react";
import { ThemeContext } from "../../ThemeContext";
import { useNavigate, useSearchParams } from "react-router-dom";
import { verifyEmail } from "../../services/api";
import {
  Box,
  Typography,
  CircularProgress,
  Button,
} from "@mui/material";

const VerifyEmailPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const didRun = useRef(false);
  const [status, setStatus] = useState("loading");
  const { darkMode } = useContext(ThemeContext);

  const token = searchParams.get("token");

  useEffect(() => {
    if (didRun.current || !token) return;
    didRun.current = true;

    verifyEmail(token)
      .then(() => setStatus("success"))
      .catch(() => setStatus("error"));
  }, [token]);

  useEffect(() => {
    if (status === "success") {
      const timer = setTimeout(() => {
        navigate("/profile");
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [status, navigate]);

  if (status === "loading") {
    return (
      <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
        <CircularProgress />
        <Typography mt={2}>Подтверждаем вашу почту...</Typography>
      </Box>
    );
  }

  if (status === "success") {
    return (
      <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
        <Typography variant="h6" gutterBottom className={`main-title ${darkMode ? "night" : "day"}`}>
          ✅ Ваш Email успешно подтверждён!
        </Typography>
        <Typography mt={2}>
          Вы будете автоматически перенаправлены в профиль через несколько секунд...
        </Typography>
        <Button onClick={() => navigate("/profile")} sx={{ mt: 2 }}>
          Перейти в профиль сейчас
        </Button>
      </Box>
    );
  }

  if (status === "invalid") {
    return (
      <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
        <Typography color="error">❌ Ссылка недействительна или отсутствует токен.</Typography>
      </Box>
    );
  }

  return (
    <Box p={4} className={`main-content ${darkMode ? "night" : "day"}`}>
      <Typography color="error">❌ Не удалось подтвердить почту. Попробуйте ещё раз.</Typography>
      <Button onClick={() => navigate("/")}>На главную</Button>
    </Box>
  );
};

export default VerifyEmailPage;
