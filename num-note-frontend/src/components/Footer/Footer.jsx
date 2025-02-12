import React, { useContext } from "react";
import { Box, Typography } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import "../../pages/MainPage/MainPage";
import "./Footer.css";

const Footer = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);
  return (
    <Box className={`footer ${darkMode ? "night" : "day"}`}>
      <Typography variant="body2">
        © 2024 Smart Calendar. All rights reserved.
      </Typography>
    </Box>
  );
};

export default Footer;
