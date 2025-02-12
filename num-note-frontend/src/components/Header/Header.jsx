import React, { useContext } from "react";
import { AppBar, Toolbar, Typography, Switch, Box, MenuItem, Select } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import { useTranslation } from "react-i18next";
import "./Header.css";

const Header = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);
  const { t, i18n } = useTranslation();

  return (
    <AppBar position="static" className={`header ${darkMode ? "night" : "day"}`}>
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          Smart Calendar
        </Typography>

        <Select
          value={i18n.language}
          onChange={(e) => {
            const lang = e.target.value;
            i18n.changeLanguage(lang);
            localStorage.setItem("language", lang);
          }}
          size="small"
        >
          <MenuItem value="en">English</MenuItem>
          <MenuItem value="ru">Русский</MenuItem>
        </Select>

        <Box display="flex" alignItems="center">
          <Typography variant="body1" sx={{ mr: 1 }}>
            {darkMode ? "Night Mode" : "Day Mode"}
          </Typography>
          <Switch checked={darkMode} onChange={toggleTheme} />
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
