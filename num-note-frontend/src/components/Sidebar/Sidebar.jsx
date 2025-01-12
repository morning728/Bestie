import React from "react";
import { Box, List, ListItem, ListItemIcon, ListItemText, Typography } from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import BarChartIcon from "@mui/icons-material/BarChart";
import SettingsIcon from "@mui/icons-material/Settings";

const Sidebar = () => {
  return (
    <Box
      sx={{
        width: 240,
        backgroundColor: "primary.main",
        color: "#fff",
        display: "flex",
        flexDirection: "column",
        height: "100vh",
        position: "fixed",
        boxShadow: 3,
      }}
    >
      {/* Логотип и название приложения */}
      <Box
        sx={{
          padding: 2,
          textAlign: "center",
          backgroundColor: "rgba(255, 255, 255, 0.1)",
        }}
      >
        <Typography variant="h5" sx={{ fontWeight: "bold" }}>
          Smart Calendar
        </Typography>
      </Box>

      {/* Список навигации */}
      <List sx={{ marginTop: 2 }}>
        <ListItem
          button
          sx={{
            "&:hover": {
              backgroundColor: "rgba(255, 255, 255, 0.2)",
            },
            padding: "10px 20px",
          }}
        >
          <ListItemIcon>
            <CalendarMonthIcon sx={{ color: "#fff" }} />
          </ListItemIcon>
          <ListItemText primary="Calendar" />
        </ListItem>

        <ListItem
          button
          sx={{
            "&:hover": {
              backgroundColor: "rgba(255, 255, 255, 0.2)",
            },
            padding: "10px 20px",
          }}
        >
          <ListItemIcon>
            <BarChartIcon sx={{ color: "#fff" }} />
          </ListItemIcon>
          <ListItemText primary="Analytics" />
        </ListItem>

        <ListItem
          button
          sx={{
            "&:hover": {
              backgroundColor: "rgba(255, 255, 255, 0.2)",
            },
            padding: "10px 20px",
          }}
        >
          <ListItemIcon>
            <SettingsIcon sx={{ color: "#fff" }} />
          </ListItemIcon>
          <ListItemText primary="Settings" />
        </ListItem>
      </List>

      {/* Нижняя часть боковой панели */}
      <Box
        sx={{
          marginTop: "auto",
          padding: 2,
          textAlign: "center",
          borderTop: "1px solid rgba(255, 255, 255, 0.2)",
        }}
      >
        <Typography variant="body2" sx={{ fontSize: "0.9rem" }}>
          Welcome, User!
        </Typography>
        <Typography variant="caption" sx={{ fontSize: "0.8rem" }}>
          Stay productive today 🚀
        </Typography>
      </Box>
    </Box>
  );
};

export default Sidebar;
