import React, { useContext, useState, useEffect } from "react";
import {
  Box,
  Grid,
  Typography,
  Button,
  Divider,
  TextField,
  MenuItem,
  IconButton,
  Autocomplete
} from "@mui/material";
import { useTranslation } from "react-i18next";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import ClearIcon from "@mui/icons-material/Whatshot"; // 🔥 Иконка для очистки
import Header from "../../components/Header/Header";
import TaskCard from "../../components/Tasks/TaskCard/TaskCard";
import AddTaskDialog from "../../components/Tasks/AddTaskDialog/AddTaskDialog";
import TaskDetailsDialog from "../../components/Tasks/TaskDetailsDialog/TaskDetailsDialog";
import { ThemeContext } from "../../ThemeContext";
import { useProjectAccess } from "../../context/ProjectAccessContext";
import { useFilteredTasksWithDates } from "../../hooks/useFilteredTasks";
import { useTasks } from "../../hooks/useTasks";
import "./MainPage.css";
import { useParams } from "react-router-dom"; // ← нужный импорт
import KanbanBoard from "../../components/KanbanBoard/KanbanBoard";
import "./MainPage.css";


const MainPage = () => {
  const { projectId } = useParams(); // <== получаем projectId из URL
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);
  const [DDView, setDDView] = useState(false);

  const [tags, setTags] = useState([]);
  const [statuses, setStatuses] = useState([]);
  const [members, setMembers] = useState([]);

  const [filterTitle, setFilterTitle] = useState("");
  const [filterTag, setFilterTag] = useState("");
  const [filterAssignee, setFilterAssignee] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [showArchived, setShowArchived] = useState(false);
  const handleToggleArchived = () => setShowArchived(prev => !prev);

  const { me, myRole, hasPermission, loading } = useProjectAccess();

  // Permissions
  const canCreateTasks = hasPermission("CAN_CREATE_TASKS");

  // Permissions

  useEffect(() => {
    if (projectId) {
      getTags().then(setTags);
      getStatuses().then(setStatuses);
      getMembers().then(setMembers);
    }
  }, [projectId]);

  const {
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
  } = useTasks(projectId); // <== передаем projectId

  const filteredTasks = useFilteredTasksWithDates(
    tasks,
    filterTag,
    filterStatus,
    filterAssignee,
    filterTitle,
    startDate,
    endDate,
    showArchived,
    statuses,
    members,
  );

  const toggleView = () => {
    setDDView((prev) => !prev);
  };

  const tagNames = tags.map(tag => tag.name);
  const statusNames = statuses.map(status => status.name);
  const memberNames = members.map(member => member.username);

  // if (loading) return <p>Загрузка...</p>;

  return (

    <Box className={`main-content ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box mt={2} sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <Typography className={`main-title ${darkMode ? "night" : "day"}`} variant="h4" gutterBottom>{t("main_page_title")}</Typography>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
          <TextField
            sx={{ width: "30em" }}
            label={t("filter_title")}
            value={filterTitle}
            onChange={(e) => setFilterTitle(e.target.value)}
            fullWidth
            margin="normal"
          />
        </Box>
        {!showArchived && (
          <Button
            variant="contained"
            color="secondary"
            onClick={toggleView} // твоя функция переключения DDView
          >
            {!DDView ? t("kanban_view") : t("list_view")}
          </Button>
        )}
        {!DDView && (
          <Button variant="contained" sx={{ backgroundcolor: "#9932CC" }} onClick={handleToggleArchived}>
            {showArchived ? t("hide_archive") : t("show_archive")}
          </Button>
        )}
        {DDView &&
          <Button disabled={!canCreateTasks} variant="contained" sx={{ backgroundcolor: "#9932CC" }} onClick={handleOpenAddDialog}>
            {t("main_page_add_task")}
          </Button>
        }
      </Box>

      {/* Фильтры */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>

        {/* Фильтры (слева) */}
        <Box display="flex" gap={2} sx={{ width: "50%" }}>
          {/* Filter Tag */}
          <Autocomplete
            sx={{ width: "50%" }}
            options={tagNames} // Передаем список строк
            value={filterTag} // Сохраняем строку
            onChange={(event, newValue) => setFilterTag(newValue)} // Сохраняем выбранную строку
            renderInput={(params) => <TextField {...params} label={t("filter_tag")} fullWidth margin="normal" />}
            freeSolo // Разрешаем вводить произвольные значения
          />

          {/* Filter Status */}
          <Autocomplete
            sx={{ width: "50%" }}
            options={statusNames} // Передаем список строк
            value={filterStatus} // Сохраняем строку
            onChange={(event, newValue) => setFilterStatus(newValue)} // Сохраняем выбранную строку
            renderInput={(params) => <TextField {...params} label={t("filter_status")} fullWidth margin="normal" />}
            freeSolo // Разрешаем вводить произвольные значения
          />

          {/* Filter Assignee */}
          <Autocomplete
            sx={{ width: "50%" }}
            options={memberNames} // Передаем список строк
            value={filterAssignee} // Сохраняем строку
            onChange={(event, newValue) => setFilterAssignee(newValue)} // Сохраняем выбранную строку
            renderInput={(params) => <TextField {...params} label={t("filter_assignee")} fullWidth margin="normal" />}
            freeSolo // Разрешаем вводить произвольные значения
          />
        </Box>

        {/* Даты (справа) */}
        <LocalizationProvider dateAdapter={AdapterDateFns}>
          <Box display="flex" gap={2} sx={{ width: "40%", alignItems: "center" }}>

            {/* Start Date + кнопка очистки */}
            <Box display="flex" alignItems="center">
              <DatePicker
                label={t("start_date")}
                value={startDate}
                onChange={setStartDate}
                renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
              />
              {startDate && (
                <IconButton onClick={() => setStartDate(null)} >
                  <ClearIcon />
                </IconButton>
              )}
            </Box>

            {/* End Date + кнопка очистки */}
            <Box display="flex" alignItems="center">
              <DatePicker
                label={t("end_date")}
                value={endDate}
                onChange={setEndDate}
                renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
              />
              {endDate && (
                <IconButton onClick={() => setEndDate(null)} >
                  <ClearIcon />
                </IconButton>
              )}
            </Box>

          </Box>
        </LocalizationProvider>

      </Box>

      <Divider sx={{ mb: 2 }} />

      {!DDView && (
        <>
          <Grid container spacing={2}>
            {filteredTasks.map((task) => (
              <Grid item xs={12} sm={6} md={4} key={task.id}>
                <TaskCard task={task} tags={tags} statuses={statuses} onClick={() => handleOpenDetailsDialog(task)} />
              </Grid>
            ))}
          </Grid>
          {!showArchived &&
            <Box mt={4} textAlign="center">
              <Button disabled={!canCreateTasks} variant="contained" color="primary" size="large" onClick={handleOpenAddDialog}>
                {t("main_page_add_task")}
              </Button>
            </Box>
          }
        </>
      )}

      <Box style={{ display: DDView ? "block" : "none" }}>
        <KanbanBoard
          tasks={filteredTasks}
          statuses={statuses}
          onCardClick={handleOpenDetailsDialog}
          onStatusChange={handleStatusChange}
        />
      </Box>


      {/* Диалоги */}
      <AddTaskDialog
        open={openAddDialog.isOpen}
        handleClose={handleCloseAddDialog}
        handleAddTask={addTask}
        handleDecompose={decomposeTaskAndRefresh}
        task={selectedTask}
        isEditing={isEditing}
        tags={tags}
        statuses={statuses}
        members={members}
      />

      {selectedTask && (
        <TaskDetailsDialog
          open={openDetailsDialog.isOpen}
          task={selectedTask}
          handleClose={handleCloseDetailsDialog}
          onEdit={editTask}
          onArchive={archiveTask}
          onRestore={restoreArchivedTask}
          statuses={statuses}
        />
      )}
    </Box>

  );
};

export default MainPage;
