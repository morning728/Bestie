import React, { createContext, useState, useEffect } from "react";
import { ThemeProvider, createTheme } from "@mui/material/styles";

// Создаем Context
export const ThemeContext = createContext();

// Обертка для переключения темы
export const ThemeContextProvider = ({ children }) => {
  const [darkMode, setDarkMode] = useState(() => {
    // Проверяем сохранённое значение темы в localStorage
    const savedTheme = localStorage.getItem("darkMode");
    return savedTheme === "true"; // true, если сохранено "true", иначе false
  });

  useEffect(() => {
    // Сохраняем текущее состояние темы в localStorage
    localStorage.setItem("darkMode", darkMode);
  }, [darkMode]);

  const theme = createTheme({
    palette: {
      mode: darkMode ? "dark" : "light",
      primary: {
        main: "#9932CC", // Новый основной цвет
      },
      secondary: {
        main: darkMode ? "#f50057" : "#FF69B4", // Приятный розовый
      },
    },
    typography: {
      fontFamily: "'Roboto', sans-serif",
    },
  });

  const toggleTheme = () => setDarkMode((prev) => !prev);

  return (
    <ThemeContext.Provider value={{ darkMode, toggleTheme }}>
      <ThemeProvider theme={theme}>{children}</ThemeProvider>
    </ThemeContext.Provider>
  );
};
