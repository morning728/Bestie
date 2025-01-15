import React, { useState, useContext } from "react";
import { Box, List, ListItem, ListItemIcon, ListItemText, Typography, IconButton } from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import BarChartIcon from "@mui/icons-material/BarChart";
import SettingsIcon from "@mui/icons-material/Settings";
import MenuIcon from "@mui/icons-material/Menu";
import { ThemeContext } from "../../ThemeContext"; // Подключаем ThemeContext
import "./Sidebar.css";

const Sidebar = () => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const { darkMode } = useContext(ThemeContext); // Получаем текущую тему

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
          <Typography variant="h5">Smart Calendar</Typography>
        </Box>
      )}

      {/* Список навигации */}
      <List className="sidebar-list">
        <ListItem className="sidebar-item" button>
          <ListItemIcon>
            <CalendarMonthIcon />
          </ListItemIcon>
          {!isCollapsed && <ListItemText primary="Calendar" />}
        </ListItem>

        <ListItem className="sidebar-item" button>
          <ListItemIcon>
            <BarChartIcon />
          </ListItemIcon>
          {!isCollapsed && <ListItemText primary="Analytics" />}
        </ListItem>

        <ListItem className="sidebar-item" button>
          <ListItemIcon>
            <SettingsIcon />
          </ListItemIcon>
          {!isCollapsed && <ListItemText primary="Settings" />}
        </ListItem>
      </List>

      {/* Нижняя часть боковой панели */}
      {!isCollapsed && (
        <Box className="sidebar-footer">
          <Typography variant="body2">Welcome, User!</Typography>
          <Typography variant="caption">Stay productive today 🚀</Typography>
        </Box>
      )}
    </Box>
  );
};

export default Sidebar;
