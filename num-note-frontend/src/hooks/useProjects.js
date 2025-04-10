import { useState, useEffect } from "react";
import { getMyProjects, createProject, updateProject, deleteProject, getFullProjectInfoById,
  addUserToProject, getProjectMembers, removeUserFromProject
 } from '../services/api';

export const useProjects = () => {
  const [projects, setProjects] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const [openAddDialog, setOpenAddDialog] = useState({ isOpen: false });
  const [openDetailsDialog, setOpenDetailsDialog] = useState({ isOpen: false });

  const fetchProjects = () => {
    getMyProjects()
      .then((response) => setProjects(response.data))
      .catch((error) => console.error("Ошибка при загрузке проектов:", error));
  };

  useEffect(() => {
    fetchProjects();
  }, []);

  const handleOpenAddDialog = () => setOpenAddDialog({ isOpen: true });

  const handleCloseAddDialog = () => {
    setOpenAddDialog({ isOpen: false });
    setIsEditing(false);
    setSelectedProject(null);
  };

  const handleSaveProject = (projectData) => {
    if (isEditing && selectedProject) {
      return editProject(selectedProject.id, projectData);
    } else {
      return addProject(projectData);
    }
  };

  const handleOpenDetailsDialog = (projectId) => {
    loadProjectDetails(projectId)
      .then((fullProjectData) => {
        setSelectedProject(fullProjectData);
        setOpenDetailsDialog({ isOpen: true });
      })
      .catch((err) => console.error('Ошибка загрузки деталей:', err));
  };


  const handleCloseDetailsDialog = () => {
    setOpenDetailsDialog({ isOpen: false });
    setSelectedProject(null);
  };

  const addProject = (newProject) => {
    return createProject(newProject).then(() => {
      fetchProjects();
      handleCloseAddDialog();
    });
  };

  const editProject = (projectId, updatedProjectData) => {
    return updateProject(projectId, updatedProjectData).then(() => {
      fetchProjects();
      handleCloseAddDialog();
    });
  };

  const setEditProject = (projectId) => {
    if (selectedProject === null) {
      loadProjectDetails(projectId)
        .then((fullProjectData) => {
          setSelectedProject(fullProjectData);
        })
    }
    setIsEditing(true);
    handleOpenAddDialog();
  };

  const removeProject = (projectId) => {
    return deleteProject(projectId).then(() => {
      fetchProjects();
      handleCloseDetailsDialog();
    });
  };

  const loadProjectDetails = (projectId) => {
    return getFullProjectInfoById(projectId)
      .then(response => response.data);
  };



  const fetchProjectMembers = (projectId) => {
    return getProjectMembers(projectId).then((response) => response.data);
  };

  const addMemberToProject = (projectId, username, roleId) => {
    return addUserToProject(projectId, username, roleId)
      .then(() => fetchProjects());
  };

  const removeMemberFromProject = (projectId, username) => {
    return removeUserFromProject(projectId, username)
      .then(() => fetchProjects());
  };




  return {
    projects,
    selectedProject,
    openAddDialog,
    openDetailsDialog,
    addProject,
    editProject,
    deleteProject: removeProject,
    fetchProjects,
    handleOpenDetailsDialog,
    handleCloseDetailsDialog,
    handleOpenAddDialog,
    handleCloseAddDialog,
    handleSaveProject,
    loadProjectDetails,
    setEditProject,
    fetchProjectMembers,
    addMemberToProject,
    removeMemberFromProject,
  };
};