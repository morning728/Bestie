import React, { useContext, useState } from "react";
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
import { useFilteredTasksWithDates } from "../../hooks/useFilteredTasks";
import { useTasks } from "../../hooks/useTasks";
import "./MainPage.css";

const MainPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t, i18n } = useTranslation();

  const tags = [
    { id: 0, name: "Health", color: "#008000" },
    { id: 1, name: "Work", color: "#ff0000" },
    { id: 2, name: "Personal", color: "#0000ff" },
    { id: 3, name: "Study", color: "#ff00ff" },
    { id: 4, name: "Business", color: "#00ffff" },
  ];

  const statuses = [
    { id: 0, name: "In Progress", color: "#ffff00" },
    { id: 1, name: "Overdue", color: "#00f00f" },
    { id: 2, name: "Completed", color: "#00ff00" },
    { id: 3, name: "Pendding", color: "#ff0000" },
  ];

  // Преобразуем массив объектов в массив строк
  const tagNames = tags.map(tag => tag.name);
  const statusNames = statuses.map(status => status.name);

  const initialTasks = [
    { id: 1, title: "Morning Meeting", is_archived: false, start_time: "09:00", end_time: "15:00", tag: "Work", status: "Completed", start_date: "2025-02-16", end_date: "2025-02-17" },
    { id: 2, title: "Workout Session", is_archived: false, start_time: "18:00", end_time: "15:00", tag: "Health", status: "Pending", start_date: "2025-02-18", end_date: "2025-02-20" },
    { id: 3, title: "Grocery Shopping", is_archived: false, start_time: "14:00", end_time: "15:00", tag: "Personal", status: "Overdue", start_date: "2025-02-21", end_date: "2025-02-24" },
    { id: 4, title: "Training", is_archived: true, start_time: "14:00", end_time: "15:00", tag: "Personal", status: "Overdue", start_date: "2025-02-21", end_date: "2025-02-24" },
    { id: 5, title: "CCooking", is_archived: true, start_time: "14:00", end_time: "15:00", tag: "Personal", status: "Overdue", start_date: "2025-02-21", end_date: "2025-02-24" },
    { id: 6, title: "Sleep", is_archived: true, start_time: "14:00", end_time: "15:00", tag: "Personal", status: "Overdue", start_date: "2025-02-21", end_date: "2025-02-24" },
  ];

  const [showArchived, setShowArchived] = useState(false);
  const [filterTag, setFilterTag] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
  const [filterTitle, setFilterTitle] = useState("");
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const {
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
  } = useTasks(initialTasks);

  const handleToggleArchived = () => {
    setShowArchived((prev) => !prev);
  };

  // Фильтрация задач по тегу, статусу и дате
  const filteredTasks = useFilteredTasksWithDates(tasks, filterTag, filterStatus, filterTitle, startDate, endDate, showArchived);

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
        <Button variant="contained" sx={{ backgroundcolor: "#9932CC" }} onClick={handleToggleArchived}>
          {showArchived ? t("hide_archive") : t("show_archive")}
        </Button>
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
                <IconButton onClick={() => setStartDate(null)} sx={{ color: "#A020F0" }}>
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
                <IconButton onClick={() => setEndDate(null)} sx={{ color: "#A020F0" }}>
                  <ClearIcon />
                </IconButton>
              )}
            </Box>

          </Box>
        </LocalizationProvider>

      </Box>

      <Divider sx={{ mb: 2 }} />

      {/* Карточки задач */}
      <Grid container spacing={2}>
        {filteredTasks.map((task) => (
          <Grid item xs={12} sm={6} md={4} key={task.id}>
            <TaskCard task={task} tags={tags} statuses={statuses} onClick={() => handleOpenDetailsDialog(task)} />
          </Grid>
        ))}
      </Grid>
      {!showArchived &&
        <Box mt={4} textAlign="center">
          <Button variant="contained" color="primary" size="large" onClick={handleOpenAddDialog}>
            {t("main_page_add_task")}
          </Button>
        </Box>
      }
      {/* Диалоги */}
      <AddTaskDialog
        open={openAddDialog.isOpen}
        handleClose={handleCloseAddDialog}
        handleAddTask={addTask}
        task={selectedTask}
        isEditing={isEditing}
      />

      {selectedTask && (
        <TaskDetailsDialog
          open={openDetailsDialog.isOpen}
          task={selectedTask}
          handleClose={handleCloseDetailsDialog}
          onEdit={editTask}
          onArchive={archiveTask}
        />
      )}
    </Box>
  );
};

export default MainPage;
