import './App.css';
import React, { useEffect, useContext } from 'react';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import {
  Box,
  Grid,
  Typography,
  Button,
  Divider,
  Switch,
  TextField,
  MenuItem,
} from "@mui/material";
import { ThemeContextProvider } from "./ThemeContext" // Импорт провайдера контекста
import MainPage from "./pages/MainPage/MainPage";
import LoginPage from "./pages/Auth/Login/LoginPage";
import RegisterPage from "./pages/Auth/Registration/RegisterPage";
import Sidebar from "./components/Sidebar/Sidebar";
import Header from "./components/Header/Header";
import Footer from "./components/Footer/Footer";
import ProfilePage from './pages/Profile/ProfilePage';
import ProjectsPage from './pages/Projects/ProjectsPage';
import ProjectSettingsPage from './pages/ProjectSettings/ProjectSettingsPage';





function App() {
  const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";
  useEffect(() => {
    document.title = 'Bestie';
  }, []);
  return (
    <ThemeContextProvider> {/* Обертывание в провайдер темы */}
      <Router>
        <Box className="main-box">
          <Sidebar /> {/* Боковая панель */}
          {/* Используем BrowserRouter вместо Router */}
          <Box className="content">
            <Routes> {/* Используем Routes для React Router v6 */}
              <Route path="/" element={<MainPage />} /> {/* Главная страница */}
              <Route path="/profile" element={<ProfilePage />} /> {/* Страница профиля */}
              <Route path="/projects" element={<ProjectsPage />} />
              <Route path="/projects/:projectId/settings" element={<ProjectSettingsPage />} />
              <Route path="/auth/login" element={<LoginPage />} /> {/* Страница входа */}
              <Route path="/auth/register" element={<RegisterPage />} /> {/* Страница регистрации */}
            </Routes>
          </Box>

        </Box>
      </Router>
      <Footer /> {/* Нижний колонтитул */}
    </ThemeContextProvider>
  );
}

export default App;
