import React, { useContext, useState, useEffect, useNavigation } from "react";
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
import { useProjects } from "../../hooks/useProjects.js";
import "./ProjectsPage.css";

const ProjectsPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t, i18n } = useTranslation();
  const navigation = useNavigation();

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
  } = useProjects();


  const filteredProjects = projects.filter((project) =>
    project.title.toLowerCase().includes(filterTitle.toLowerCase())
  );

  return (
    <Box className={`project-content ${darkMode ? "night" : "day"}`}>
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
          className={`project-title ${darkMode ? "night" : "day"}`}
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
                onEdit={() => navigation.navigate(`/projects/${project.id}/settings`)}
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
          onEdit={navigation.navigate(`/projects/${selectedProject.id}/settings`)}
          onDelete={deleteProject}
        />
      )}
    </Box>
  );
};

export default ProjectsPage;
