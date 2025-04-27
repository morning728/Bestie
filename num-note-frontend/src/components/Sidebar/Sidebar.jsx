import React, { useState, useContext, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Box, List, ListItem, ListItemIcon, ListItemText, Typography, IconButton } from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import BarChartIcon from "@mui/icons-material/BarChart";
import SettingsIcon from "@mui/icons-material/Settings";
import MenuIcon from "@mui/icons-material/Menu";
import PersonIcon from "@mui/icons-material/Person"; // Иконка для профиля
import HomeIcon from "@mui/icons-material/Home"; // Иконка для главной страницы
import { ThemeContext } from "../../ThemeContext"; // Подключаем ThemeContext
import { Link } from "react-router-dom"; // Для перехода между страницами
import "./Sidebar.css";
import { useLocation } from "react-router-dom";
import AssignmentIcon from "@mui/icons-material/Assignment"; // иконка задач
import ExpandLess from "@mui/icons-material/ExpandLess";
import ExpandMore from "@mui/icons-material/ExpandMore";
import { useProjectsContext } from "../../context/ProjectsContext";
import Button from "@mui/material/Button"; // Импортируем Button
import { useNavigate } from "react-router-dom"; // Для переходов
import {forceLogout} from "../../services/api.js";



const Sidebar = () => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const { darkMode } = useContext(ThemeContext); // Получаем текущую тему
  const { t, i18n } = useTranslation();
  const { projects, fetchProjects } = useProjectsContext();
  const navigate = useNavigate();
  const isAuthenticated = !!localStorage.getItem("token"); // Проверка наличия токена
  useEffect(() => {
    fetchProjects();
  }, []);


  const location = useLocation();

  const toggleSidebar = () => {
    setIsCollapsed((prev) => !prev);
  };

  const [projectsExpanded, setProjectsExpanded] = useState(false);



  return (
    <Box
      className={`sidebar ${isCollapsed ? "collapsed" : "expanded"} ${darkMode ? "dark" : "light"}`}
    >
      {/* Кнопка для сворачивания */}
      <Box className="sidebar-toggle">
        <IconButton onClick={toggleSidebar} color="inherit">
          <MenuIcon />
        </IconButton>
      </Box>

      {/* Логотип и название */}
      {!isCollapsed && (
        <Box className="sidebar-header">
          <Typography variant="h5">
            {/* Логотип с переходом на главную страницу */}
            <Link to="/" style={{ textDecoration: "none", color: "inherit" }}>
              Smart Calendar
            </Link>
          </Typography>
        </Box>
      )}
      <Box className="sidebar-scrollable">
        {/* Список навигации */}
        <List className="sidebar-list">
          {/* Кнопка на главную страницу */}
          <ListItem className="sidebar-item" button component={Link} to="/">
            <ListItemIcon>
              <HomeIcon />
            </ListItemIcon>
            {!isCollapsed && <ListItemText primary={t("home")} />}
          </ListItem>

          <ListItem className="sidebar-item" button component={Link} to="/calendar">
            <ListItemIcon>
              <CalendarMonthIcon />
            </ListItemIcon>
            {!isCollapsed && <ListItemText primary={t("calendar")} />}
          </ListItem>

          <ListItem className="sidebar-item" button component={Link} to="/analytics">
            <ListItemIcon>
              <BarChartIcon />
            </ListItemIcon>
            {!isCollapsed && <ListItemText primary={t("analytics")} />}
          </ListItem>

          <ListItem className="sidebar-item" button component={Link} to="/settings">
            <ListItemIcon>
              <SettingsIcon />
            </ListItemIcon>
            {!isCollapsed && <ListItemText primary={t("settings")} />}
          </ListItem>

          {/* Кнопка для перехода в профиль */}
          <ListItem className="sidebar-item" button component={Link} to="/profile">
            <ListItemIcon>
              <PersonIcon />
            </ListItemIcon>
            {!isCollapsed && <ListItemText primary={t("profile")} />}
          </ListItem>

          <ListItem className="sidebar-item">
            <ListItemIcon>
              <AssignmentIcon />
            </ListItemIcon>
            {!isCollapsed && (
              <>
                <ListItemText
                  primary={
                    <Link
                      to="/projects"
                      style={{
                        textDecoration: "none",
                        color: darkMode ? "#ccc" : "#2c2c54",

                      }}
                    >
                      {t("projects_page_title")}
                    </Link>
                  }
                />
                <IconButton size="small" onClick={() => setProjectsExpanded(!projectsExpanded)}>
                  {projectsExpanded ? <ExpandLess fontSize="small" /> : <ExpandMore fontSize="small" />}
                </IconButton>
              </>
            )}
          </ListItem>

          {/* Вложенные проекты */}
          {projectsExpanded && !isCollapsed && (
            <Box sx={{ ml: 4, width: "70%" }}>
              {projects.map((project) => (
                <ListItem
                  key={project.id}
                  button
                  component={Link}
                  to={`/projects/${project.id}/tasks`}
                  className="sidebar-project-item"
                >
                  <ListItemIcon><AssignmentIcon fontSize="small" /></ListItemIcon>
                  <ListItemText
                    primary={project.title}
                    primaryTypographyProps={{
                      fontSize: "0.85rem",
                      color: darkMode ? "#ccc" : "#2c2c54",
                      noWrap: true, // ❗️ Это добавляет ellipsis
                      title: project.title // ✨ Tooltip
                    }}
                  />
                </ListItem>
              ))}
            </Box>
          )}

        </List>
      </Box>




      {/* Нижняя часть боковой панели */}
      {!isCollapsed && (
        <Box className="sidebar-footer" display="flex" flexDirection="column" alignItems="center" gap={1}>
          <Typography variant="body2">{t("welcome")}</Typography>
          <Typography variant="caption">{t("stay_productive")} 🚀</Typography>

          <Box display="flex" flexDirection="column" gap={1} mt={2}>
            {!isAuthenticated ? (
              <>
                <Button variant="outlined" size="small" onClick={() => navigate("/auth/login")}>
                  {t("login")}
                </Button>
                <Button variant="outlined" size="small" onClick={() => navigate("/auth/register")}>
                  {t("register")}
                </Button>
              </>
            ) : (
              <Button
                variant="contained"
                color="error"
                size="small"
                onClick={() => {
                  forceLogout();
                }}
              >
                {t("logout")}
              </Button>
            )}
          </Box>
        </Box>
      )}
    </Box>
  );
};

export default Sidebar;
