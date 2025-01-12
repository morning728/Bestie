import React, { useContext } from "react";
import { AppBar, Toolbar, Typography, Switch, Box } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import "./Header.css";

const Header = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);

  return (
    <AppBar position="static" className={`header ${darkMode ? "night" : "day"}`}>
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>
          Smart Calendar
        </Typography>
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
