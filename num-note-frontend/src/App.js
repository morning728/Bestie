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
import { ProjectsProvider } from "./context/ProjectsContext";
import ProjectSettingsPage from './pages/ProjectSettings/ProjectSettingsPage';
import AcceptInvitePage from './pages/ProjectSettings/invitation/AcceptInvitePage ';
import ProjectAccessWrapper from './context/ProjectAccessWrapper';





function App() {
  const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";
  useEffect(() => {
    document.title = 'Bestie';
  }, []);
  return (
    <ThemeContextProvider>
      <Router>
        <Routes>

          {/* 👇 Публичные страницы — без ProjectsProvider */}
          <Route path="/auth/login" element={<LoginPage />} />
          <Route path="/auth/register" element={<RegisterPage />} />
          <Route path="/accept-invite" element={<AcceptInvitePage />} />
          <Route path="/accept-universal" element={<AcceptInvitePage isUniversal />} />

          {/* 👇 Приватный layout со всеми нужными провайдерами */}
          <Route
            path="/*"
            element={
              <ProjectsProvider>
                <Box className="main-box">
                  <Sidebar />
                  <Box className="content">
                    <Routes>
                      <Route path="/projects/:projectId/tasks" element={<MainPage />} />
                      <Route
                        path="/projects/:projectId/settings"
                        element={
                          <ProjectAccessWrapper>
                            <ProjectSettingsPage />
                          </ProjectAccessWrapper>
                        }
                      />
                      <Route path="/projects" element={<ProjectsPage />} />
                      <Route path="/profile" element={<ProfilePage />} />
                    </Routes>
                  </Box>
                </Box>
                <Footer />
              </ProjectsProvider>
            }
          />

        </Routes>
      </Router>
    </ThemeContextProvider>
  );
}

export default App;
