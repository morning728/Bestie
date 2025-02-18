import React, { useContext, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  Button,
  Divider,
  TextField,
  MenuItem,
  IconButton
} from "@mui/material";
import { useTranslation } from "react-i18next";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import ClearIcon from "@mui/icons-material/Whatshot"; // 🔥 Иконка для очистки
import Header from "../../components/Header/Header";
import TaskCard from "../../components/TaskCard/TaskCard";
import AddTaskDialog from "../../components/AddTaskDialog/AddTaskDialog";
import TaskDetailsDialog from "../../components/TaskDetailsDialog/TaskDetailsDialog";
import { ThemeContext } from "../../ThemeContext";
import { useFilteredTasksWithDates } from "../../hooks/useFilteredTasks";
import { useTasks } from "../../hooks/useTasks";
import "./MainPage.css";

const MainPage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t, i18n } = useTranslation();

  const initialTasks = [
    { id: 1, title: "Morning Meeting", time: "09:00", tag: "Work", status: "Completed", start_date: "2025-02-16", end_date: "2025-02-17" },
    { id: 2, title: "Workout Session", time: "18:00", tag: "Health", status: "Pending", start_date: "2025-02-18", end_date: "2025-02-20" },
    { id: 3, title: "Grocery Shopping", time: "14:00", tag: "Personal", status: "Overdue", start_date: "2025-02-21", end_date: "2025-02-24" }
  ];

  const [filterTag, setFilterTag] = useState("");
  const [filterStatus, setFilterStatus] = useState("");
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
    deleteTask,
    handleOpenAddDialog,
    handleOpenDetailsDialog,
    handleCloseAddDialog,
    handleCloseDetailsDialog
  } = useTasks(initialTasks);

  // Фильтрация задач по тегу, статусу и дате
  const filteredTasks = useFilteredTasksWithDates(tasks, filterTag, filterStatus, startDate, endDate);

  return (
    <Box className={`main-content ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box mt={2} sx={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <Typography variant="h4" gutterBottom>{t("main_page_title")}</Typography>
      </Box>

      {/* Фильтры */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>

        {/* Фильтры (слева) */}
        <Box display="flex" gap={2} sx={{ width: "50%" }}>
          <TextField
            select
            label="Filter Tag"
            fullWidth
            margin="normal"
            value={filterTag}
            onChange={(e) => setFilterTag(e.target.value)}
          >
            <MenuItem value="">{t("main_page_all")}</MenuItem>
            <MenuItem value="Work">Work</MenuItem>
            <MenuItem value="Health">Health</MenuItem>
            <MenuItem value="Personal">Personal</MenuItem>
            <MenuItem value="Study">Study</MenuItem>
          </TextField>

          <TextField
            select
            label="Filter Status"
            fullWidth
            margin="normal"
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
          >
            <MenuItem value="">All</MenuItem>
            <MenuItem value="Completed">Completed</MenuItem>
            <MenuItem value="Pending">Pending</MenuItem>
            <MenuItem value="In Progress">In Progress</MenuItem>
            <MenuItem value="Overdue">Overdue</MenuItem>
          </TextField>
        </Box>

        {/* Даты (справа) */}
        <LocalizationProvider dateAdapter={AdapterDateFns}>
          <Box display="flex" gap={2} sx={{ width: "40%", alignItems: "center" }}>

            {/* Start Date + кнопка очистки */}
            <Box display="flex" alignItems="center">
              <DatePicker
                label={t("Start_date")}
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
                label={t("End_date")}
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
            <TaskCard task={task} onClick={() => handleOpenDetailsDialog(task)} />
          </Grid>
        ))}
      </Grid>

      <Box mt={4} textAlign="center">
        <Button variant="contained" color="primary" size="large" onClick={handleOpenAddDialog}>
          + Add Task
        </Button>
      </Box>

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
          onDelete={deleteTask}
        />
      )}
    </Box>
  );
};

export default MainPage;
