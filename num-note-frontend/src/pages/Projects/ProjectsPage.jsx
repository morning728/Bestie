import React, { useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Grid,
  Typography,
  Button,
  Divider,
  TextField,
  IconButton,
  CircularProgress
} from "@mui/material";
import { useTranslation } from "react-i18next";
import Header from "../../components/Header/Header";
import ProjectCard from "../../components/Projects/ProjectCard/ProjectCard.jsx";
import AddProjectDialog from "../../components/Projects/AddProjectDialog/AddProjectDialog.jsx";
import ProjectDetailsDialog from "../../components/Projects/ProjectDetailsDialog/ProjectDetailsDialog.jsx";
import { ThemeContext } from "../../ThemeContext";
import { useProjectsContext } from "../../context/ProjectsContext";
import "./ProjectsPage.css";

const ProjectsPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();

  const [filterTitle, setFilterTitle] = useState("");

  const {
    projects,
    isEditing,
    selectedProject,
    openAddDialog,
    openDetailsDialog,
    addProject,
    editProject,
    deleteProject,
    fetchProjects,
    handleOpenAddDialog,
    handleOpenDetailsDialog,
    handleCloseAddDialog,
    handleCloseDetailsDialog,
    handleSaveProject,
    setEditProject,
  } = useProjectsContext();


  const filteredProjects = projects.filter((project) =>
    project.title.toLowerCase().includes(filterTitle.toLowerCase())
  );

  return (
    <Box className={`main-content ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box
        mt={2}
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Typography
          className={`main-title ${darkMode ? "night" : "day"}`}
          variant="h4"
          gutterBottom
        >
          {t("project_page_title")}
        </Typography>
        <Button
          variant="contained"
          className="add-project-btn"
          onClick={handleOpenAddDialog}
        >
          {t("add_project")}
        </Button>
      </Box>

      <Box mb={2}>
        <TextField
          label={t("search_project")}
          value={filterTitle}
          onChange={(e) => setFilterTitle(e.target.value)}
          fullWidth
          margin="normal"
        />
      </Box>

      <Divider sx={{ mb: 2 }} />

      {projects.length === 0 ? (
        <Box sx={{ textAlign: 'center', mt: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={2}>
          {filteredProjects.map((project) => (
            <Grid item xs={12} sm={6} md={4} key={project.id}>
              <ProjectCard
                project={project}
                onClick={() => handleOpenDetailsDialog(project.id)}
                onEdit={() => navigate(`/projects/${project.id}/settings`)}
              />
            </Grid>
          ))}
        </Grid>
      )}

      <AddProjectDialog
        open={openAddDialog.isOpen}
        handleClose={handleCloseAddDialog}
        handleAddProject={addProject}
        onSave={handleSaveProject}
        project={selectedProject}
        isEditing={isEditing}
      />

      {selectedProject && (
        <ProjectDetailsDialog
          open={openDetailsDialog.isOpen}
          project={selectedProject}
          handleClose={handleCloseDetailsDialog}
          onEdit={() => navigate(`/projects/${selectedProject.id}/settings`)}
          onDelete={deleteProject}
        />
      )}
    </Box>
  );
};

export default ProjectsPage;
