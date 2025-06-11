import { useState, useEffect } from "react";
import { useModal } from "./useModal";
import {
  getTasksByProject,
  createTask,
  updateTask,
  archiveTask as archiveTaskApi,
  getProjectTags,
  getProjectStatuses,
  restoreTask,
  getProjectMembers,
  decomposeTask
} from "../services/api";

export const useTasks = (projectId) => {
  const [tasks, setTasks] = useState([]);
  const [selectedTask, setSelectedTask] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const openAddDialog = useModal();
  const openDetailsDialog = useModal();

  useEffect(() => {
    if (projectId) fetchTasks();
  }, [projectId]);

  const fetchTasks = async () => {
    try {
      const response = await getTasksByProject(projectId);
      setTasks(response.data);
    } catch (error) {
      console.error("Ошибка при загрузке задач:", error);
    }
  };

  const addTask = async (taskData) => {
    try {
      let createdTask = null;

      if (isEditing && selectedTask) {
        const response = await updateTask(selectedTask.id, taskData);
        createdTask = response.data; // 💡 получили задачу с id
      } else {
        const response = await createTask({ ...taskData, projectId });
        createdTask = response.data; // 💡 получили задачу с id
      }

      return createdTask;
    } catch (error) {
      console.error("Ошибка при добавлении/редактировании задачи:", error);
      return null;
    } finally {
      await fetchTasks();
      handleCloseAddDialog();
    }
  };

  const decomposeTaskAndRefresh = (taskData, count) => {
    handleCloseAddDialog();

    // Можешь здесь показать Snackbar или alert, если хочешь

    decomposeTask({ ...taskData, projectId }, count)
      .then(() => {
        fetchTasks();
      })
      .catch((error) => {
        alert("Ошибка при декомпозиции", error);
        // но не даём ошибке уйти в глобальный обработчик
      });

  };

  const editTask = (task) => {
    setSelectedTask(task);
    setIsEditing(true);
    openAddDialog.openModal();
  };

  const archiveTask = async (taskId) => {
    try {
      await archiveTaskApi(taskId);
    } catch (error) {
      console.error("Ошибка при архивировании:", error);
    } finally {
      await fetchTasks();
      handleCloseDetailsDialog();
    }
  };

  const restoreArchivedTask = async (taskId) => {
    try {
      await restoreTask(taskId);
    } catch (error) {
      console.error("Ошибка восстановления задачи:", error);
    } finally {
      await fetchTasks();
      handleCloseDetailsDialog();
    }
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

  const handleStatusChange = async (taskId, newStatusId) => {
    const task = tasks.find(t => t.id === taskId);
    if (!task) return;
    const tagIds = task.tags?.map(tag => tag.id);
    const assigneeIds = task.assignees?.map(a => a.userId);
    const updatedTask = {
      ...task,
      statusId: newStatusId,
      tagIds: tagIds,
      assigneeIds: assigneeIds,
    };

    try {
      await updateTask(taskId, updatedTask);
    } catch (err) {
      console.error("Ошибка при смене статуса:", err);
    } finally {
      await fetchTasks(); // обновляем список после успешного обновления
    }
  };

  const getTags = () => getProjectTags(projectId).then(res => res.data);
  const getStatuses = () => getProjectStatuses(projectId).then(res => res.data);
  const getMembers = () => getProjectMembers(projectId).then(res => res.data)

  return {
    tasks,
    isEditing,
    selectedTask,
    openAddDialog,
    openDetailsDialog,
    addTask,
    decomposeTaskAndRefresh,
    editTask,
    archiveTask,
    restoreArchivedTask,
    handleOpenAddDialog,
    handleOpenDetailsDialog,
    handleCloseAddDialog,
    handleCloseDetailsDialog,
    getTags,
    getStatuses,
    getMembers,
    handleStatusChange,
  };
};
