import React, { useState, useContext } from "react";
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

const Sidebar = () => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const { darkMode } = useContext(ThemeContext); // Получаем текущую тему
  const { t, i18n } = useTranslation();

  const toggleSidebar = () => {
    setIsCollapsed((prev) => !prev);
  };

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

        {/* Кнопка для перехода в архив */}
        <ListItem className="sidebar-item" button component={Link} to="/projects">
          <ListItemIcon>
            <PersonIcon />
          </ListItemIcon>
          {!isCollapsed && <ListItemText primary={t("projects_page_title")} />}
        </ListItem>
      </List>

      {/* Нижняя часть боковой панели */}
      {!isCollapsed && (
        <Box className="sidebar-footer">
          <Typography variant="body2">{t("welcome")}</Typography>
          <Typography variant="caption">{t("stay_productive")} 🚀</Typography>
        </Box>
      )}
    </Box>
  );
};

export default Sidebar;
