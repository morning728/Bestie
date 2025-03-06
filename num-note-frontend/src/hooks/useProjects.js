import { useState } from "react";

export const useProjects = () => {
  const [projects, setProjects] = useState([
    {
      id: 1,
      title: "AI Research",
      description: "A project focused on developing AI algorithms.",
      members: ["John Doe", "Jane Smith"],
      status: "Active",
    },
    {
      id: 2,
      title: "Web Development",
      description: "Creating a robust web application.",
      members: ["Alice Brown", "Bob White"],
      status: "Completed",
    },
  ]);

  const [isEditing, setIsEditing] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null);
  const [openAddDialog, setOpenAddDialog] = useState({ isOpen: false });
  const [openDetailsDialog, setOpenDetailsDialog] = useState({ isOpen: false });

  const addProject = (newProject) => {
    if (isEditing) {
      setProjects((prev) => prev.map((p) => (p.id === newProject.id ? newProject : p)));
    } else {
      setProjects((prev) => [...prev, { ...newProject, id: prev.length + 1 }]);
    }
    handleCloseAddDialog();
  };

  const editProject = (project) => {
    setSelectedProject(project);
    setIsEditing(true);
    handleOpenAddDialog();
  };

  const deleteProject = (projectId) => {
    setProjects((prev) => prev.filter((project) => project.id !== projectId));
    handleCloseDetailsDialog();
  };

  const handleOpenAddDialog = () => setOpenAddDialog({ isOpen: true });
  const handleCloseAddDialog = () => {
    setOpenAddDialog({ isOpen: false });
    setIsEditing(false);
    setSelectedProject(null);
  };

  const handleOpenDetailsDialog = (project) => {
    setSelectedProject(project);
    setOpenDetailsDialog({ isOpen: true });
  };

  const handleCloseDetailsDialog = () => {
    setOpenDetailsDialog({ isOpen: false });
    setSelectedProject(null);
  };
  
  const handleSaveProject = (newProject) => {
    setProjects((prevProjects) => {
      if (isEditing) {
        setIsEditing(false);
        // ✅ Редактируем существующий проект
        return prevProjects.map((p) => (p.id === selectedProject.id ? newProject : p));
      } else {
        // ✅ Добавляем новый проект с уникальным ID
        return [...prevProjects, { ...newProject, id: prevProjects.length + 1 }];
      }
    });
    handleCloseAddDialog(); // Закрываем модальное окно после сохранения
  };
  const removeMember = (projectId, member) => {
    setProjects((prev) =>
      prev.map((p) =>
        p.id === projectId
          ? { ...p, members: p.members.filter((m) => m !== member) }
          : p
      )
    );
  };

  return {
    projects,
    isEditing,
    selectedProject,
    openAddDialog,
    openDetailsDialog,
    addProject,
    editProject,
    deleteProject,
    handleOpenAddDialog,
    handleCloseAddDialog,
    handleSaveProject,
    handleOpenDetailsDialog,
    handleCloseDetailsDialog,
    removeMember
  };
};
