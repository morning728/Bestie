import React, { useContext, useEffect, useState } from "react";
import { Tabs, Tab, Box, Typography } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import { useTranslation } from "react-i18next";
import "./ProjectSettingsPage.css";
import Header from "../../components/Header/Header";
import { useParams } from "react-router-dom";
import { getFullProjectInfoById } from "../../services/api";
import GeneralSettingsTab from "./tabs/GeneralSettingsTab";
import MembersTab from "./tabs/MembersTab";
import RolesTab from "./tabs/RolesTab";
import TagsTab from "./tabs/TagsTab";
import StatusesTab from "./tabs/StatusesTab";
import ResourcesTab from "./tabs/ResourcesTab";
import { ProjectAccessProvider } from "../../context/ProjectAccessContext";


const ProjectSettingsPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();
  const { projectId } = useParams();
  const [tabIndex, setTabIndex] = useState(0);
  const [project, setProject] = useState(null);
  const tabCount = 6; // или сколько у тебя вкладок
  const gradientPosition = `${100 - (tabIndex / (tabCount - 1)) * 100}%`;

  useEffect(() => {
    getFullProjectInfoById(projectId).then((res) => setProject(res.data));
  }, [projectId]);

  const handleTabChange = (_, newValue) => setTabIndex(newValue);

  if (!project) return <Typography>{t("loading")}</Typography>;

  return (
    <Box className={`main-content ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box className="settings-container">
        <Tabs
          value={tabIndex}
          onChange={handleTabChange}
          variant="fullWidth" // <— ключевой параметр
          sx={{
            position: "relative",
            "&::before": {
              content: '""',
              position: "absolute",
              bottom: 0,
              left: 0,
              right: 0,
              height: "3px",
              backgroundImage: darkMode
                ? "radial-gradient(circle at center,rgb(83, 233, 239) 0%, transparent 60%)"
                : "radial-gradient(circle at center,rgb(251, 41, 146) 0%, transparent 60%)",
              backgroundRepeat: "no-repeat",
              backgroundSize: "200% 100%",
              backgroundPosition: `${gradientPosition} 100%`,
              transition: "background-position 0.4s ease-in-out",
            },
            ".MuiTabs-indicator": {
              display: "none",
            },
            ".MuiTab-root": {
              color: darkMode ? "#aaa" : "rgb(251, 41, 146)",
              fontWeight: 500,
              textTransform: "none",
              mx: 1,
              px: 2,
              transition: "color 0.3s",
            },
            ".Mui-selected": {
              color: darkMode ? "#00f6ff !important" : "#d81b60 !important", // Явно!
              textShadow: darkMode
                ? "0 0 8px #00f6ff"
                : "0 0 8px rgb(177, 7, 143)",
            },

          }}
        >
          <Tab label={t("general")} />
          <Tab label={t("members")} />
          <Tab label={t("roles")} />
          <Tab label={t("tags")} />
          <Tab label={t("statuses")} />
          <Tab label={t("resources")} />
        </Tabs>
        <ProjectAccessProvider projectId={projectId}>
          <Box className="tab-content">
            {tabIndex === 0 && <GeneralSettingsTab project={project} />}
            {tabIndex === 1 && <MembersTab projectId={projectId} />}
            {tabIndex === 2 && <RolesTab projectId={projectId} />}
            {tabIndex === 3 && <TagsTab projectId={projectId} />}
            {tabIndex === 4 && <StatusesTab projectId={projectId} />}
            {tabIndex === 5 && <ResourcesTab projectId={projectId} />}
            {/* Остальные вкладки будут добавлены позже */}
          </Box>
        </ProjectAccessProvider>
      </Box>
    </Box>
  );
};

export default ProjectSettingsPage;
