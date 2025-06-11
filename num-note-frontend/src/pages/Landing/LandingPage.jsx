import React, { useContext, useEffect } from "react";
import { Box, Button, Typography, Grid, Paper } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import { useTranslation } from "react-i18next";
import Header from "../../components/Header/Header";
import "./LandingPage.css";
import AOS from "aos";
import "aos/dist/aos.css";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom"; // Импортируем Link

const LandingPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();
  const navigate = useNavigate();

  useEffect(() => {
    AOS.init({ duration: 1000, easing: "ease-in-out" });
  }, []);

  return (
    <Box className={`landing-page ${darkMode ? "night" : "day"}`}>
      <Header />

      {/* Hero Section */}
      <Box className="hero-section" data-aos="fade-up">
        <Typography variant="h3" className="hero-title">
          {t("hero.title")}
        </Typography>
        <Typography variant="h5" className="hero-subtitle">
          {t("hero.subtitle")}
        </Typography>
        <Link to="/auth/register">
          <Button variant="contained" color="primary" className="cta-button">
            {t("cta.getStarted")}
          </Button>
        </Link>
      </Box>

      {/* Services Section */}
      <Box className="services-section" data-aos="fade-up">
        <Typography variant="h4" className="section-title">
          {t("services.title")}
        </Typography>
        <Grid container spacing={4} justifyContent="center">
          <Grid item xs={12} sm={3} className="service-card" data-aos="flip-up">
            <Paper className="service-paper">
              <Typography variant="h5" className="service-title">
                {t("services.aiDecomposition")}
              </Typography>
              <Typography className="service-description">
                {t("services.aiDescription")}
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={3} className="service-card" data-aos="flip-up">
            <Paper className="service-paper">
              <Typography variant="h5" className="service-title">
                {t("services.multiView")}
              </Typography>
              <Typography className="service-description">
                {t("services.multiViewDescription")}
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={3} className="service-card" data-aos="flip-up">
            <Paper className="service-paper">
              <Typography variant="h5" className="service-title">
                {t("services.notifications")}
              </Typography>
              <Typography className="service-description">
                {t("services.notificationsDescription")}
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Box>

      {/* Animation Section */}
      <Box className="animation-section" data-aos="zoom-in-up">
        <Typography variant="h4" className="section-title">
          {t("animation.title")}
        </Typography>
        <Box className="animation-container">
          <div className="task-flow-animation"></div>
          <div className="task-data-animation"></div>
        </Box>
        <Typography className="section-subtitle">
          {t("animation.subtitle")}
        </Typography>
      </Box>

      {/* Call-to-Action Section */}
      <Box className="cta-section" data-aos="fade-up">
        <Typography variant="h4" className="cta-title">
          {t("cta.title")}
        </Typography>
        <Typography className="cta-description">
          {t("cta.description")}
        </Typography>
        <Link to="/auth/register">
          <Button variant="contained" color="primary" className="cta-button">
            {t("cta.startNow")}
          </Button>
        </Link>
      </Box>
    </Box>
  );
};

export default LandingPage;
