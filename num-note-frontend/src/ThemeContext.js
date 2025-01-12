import React, { createContext, useState } from "react";
import { ThemeProvider, createTheme } from "@mui/material/styles";

// Создаем Context
export const ThemeContext = createContext();

// Обертка для переключения темы
export const ThemeContextProvider = ({ children }) => {
  const [darkMode, setDarkMode] = useState(false);

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

  const toggleTheme = () => setDarkMode(!darkMode);

  return (
    <ThemeContext.Provider value={{ darkMode, toggleTheme }}>
      <ThemeProvider theme={theme}>{children}</ThemeProvider>
    </ThemeContext.Provider>
  );
};
