import { useState } from "react";
import {useModal} from "./useModal";
import { motion } from "framer-motion";

export const useTasks = (initialTasks = []) => {

  const [tasks, setTasks] = useState(initialTasks);

  const [selectedTask, setSelectedTask] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const openAddDialog = useModal();
  const openDetailsDialog = useModal();



  const addTask = (newTask) => {
    if (isEditing) {
      setTasks((prev) => prev.map((t) => (t.id === newTask.id ? newTask : t)));
    } else {
      setTasks((prev) => [...prev, { ...newTask, id: prev.length + 1, is_archived: false }]);
    }
    handleCloseAddDialog();
  };

  const editTask = (task) => {
    setSelectedTask(task);
    setIsEditing(true);
    openAddDialog.openModal();
  };

  const archiveTask = (taskId) => {
    setTasks((prev) =>
        prev.map((task) =>
            task.id === taskId ? { ...task, is_archived: true } : task
        )
    );
    handleCloseDetailsDialog();
};

  const handleOpenAddDialog = () => {
    setSelectedTask(null);
    setIsEditing(false);
    openAddDialog.openModal();
  };

  const handleCloseAddDialog = () => {
    openAddDialog.closeModal();
    setIsEditing(false);
    setSelectedTask(null);
  };

  const handleOpenDetailsDialog = (task) => {
    setSelectedTask(task);
    openDetailsDialog.openModal();
  };

  const handleCloseDetailsDialog = () => {
    openDetailsDialog.closeModal();
    setSelectedTask(null);
  };

  return {
    tasks,
    isEditing,
    selectedTask,
    openAddDialog,
    openDetailsDialog,
    addTask,
    editTask,
    archiveTask,
    handleOpenAddDialog,
    handleOpenDetailsDialog,
    handleCloseAddDialog,
    handleCloseDetailsDialog
  };





  // const [tasks, setTasks] = useState(initialTasks);

  // const addTask = (newTask) => {
  //   setTasks((prev) => [...prev, { ...newTask, id: prev.length + 1 }]);
  // };

  // const editTask = (updatedTask) => {
  //   setTasks((prev) => prev.map((task) => (task.id === updatedTask.id ? updatedTask : task)));
  // };

  // const deleteTask = (taskId) => {
  //   setTasks((prev) => prev.filter((task) => task.id !== taskId));
  // };

  // return {
  //   tasks,
  //   addTask,
  //   editTask,
  //   deleteTask,
  // };
};