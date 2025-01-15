import React, { useContext, useState } from "react";
import {
  Box,
  Grid,
  Typography,
  Button,
  Divider,
  Switch,
  TextField,
  MenuItem,
} from "@mui/material";
import Header from "../../components/Header/Header";
import TaskCard from "../../components/TaskCard/TaskCard";
import AddTaskDialog from "../../components/AddTaskDialog/AddTaskDialog";
import TaskDetailsDialog from "../../components/TaskDetailsDialog/TaskDetailsDialog";
import AnimatedModal from "../../components/AnimatedModal/AnimatedModal";
import { ThemeContext } from "../../ThemeContext";
import { useFilteredTasks } from "../../hooks/useFilteredTasks";
import { useTasks } from "../../hooks/useTasks";
import "./MainPage.css";

const MainPage = () => {
  const { darkMode, toggleTheme } = useContext(ThemeContext);

  const initialTasks = [
    {
      id: 1,
      title: "Morning Meeting",
      time: "09:00",
      tag: "Work",
      description: "Discuss project updates with the team.",
      priority: "High",
      status: "Completed",
      reminder: true,
      reminderTime: "08:45",
    },
    {
      id: 2,
      title: "Workout Session",
      time: "18:00",
      tag: "Health",
      description: "Do a 30-minute cardio workout.",
      priority: "Medium",
      status: "Pending",
      reminder: false,
      reminderTime: "",
    },
    {
      id: 3,
      title: "Grocery Shopping",
      time: "14:00",
      tag: "Personal",
      description: "Buy ingredients for dinner.",
      priority: "Low",
      status: "Overdue",
      reminder: true,
      reminderTime: "13:30",
    },
  ];




  const [filterTag, setFilterTag] = useState("");
  const [filterStatus, setFilterStatus] = useState("");

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
    handleCloseDetailsDialog } = useTasks(initialTasks);


  const filteredTasks = useFilteredTasks(tasks, filterTag, filterStatus);

  return (
    <Box className={`main-content ${darkMode ? "night" : "day"}`}>
      <Header />
      <Box mt={2} sx={{ display: "flex", justifyContent: "space-between" }}>
        <Typography variant="h4" gutterBottom>
          Today's Tasks
        </Typography>
        {/* <Box display="flex" alignItems="center">
          <Typography variant="body1" sx={{ mr: 1 }}>
            {darkMode ? "Night Mode" : "Day Mode"}
          </Typography>
          <Switch checked={darkMode} onChange={toggleTheme} />
        </Box> */}
      </Box>

      <Box display="flex" justifyContent="space-around" mb={2}>
        <TextField
          select
          name="filter-tag"
          label="Filter Tag"
          fullWidth
          margin="normal"
          value={filterTag}
          onChange={(e) => setFilterTag(e.target.value)}
          sx={{ width: "40%" }}
        >
          <MenuItem value="">All</MenuItem>
          <MenuItem value="Work">Work</MenuItem>
          <MenuItem value="Health">Health</MenuItem>
          <MenuItem value="Personal">Personal</MenuItem>
          <MenuItem value="Study">Study</MenuItem>
        </TextField>
        <TextField
          select
          name="filter-status"
          label="Filter Status"
          fullWidth
          margin="normal"
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          sx={{ width: "40%" }}
        >
          <MenuItem value="">All</MenuItem>
          <MenuItem value="Completed">Completed</MenuItem>
          <MenuItem value="Pending">Pending</MenuItem>
          <MenuItem value="In Progress">In Progress</MenuItem>
          <MenuItem value="Overdue">Overdue</MenuItem>
        </TextField>
      </Box>

      <Divider sx={{ mb: 2 }} />

      <Grid container spacing={2}>
        {filteredTasks.map((task) => (
          <Grid item xs={12} sm={6} md={4} key={task.id}>
            <TaskCard task={task} onClick={() => handleOpenDetailsDialog(task)} />
          </Grid>
        ))}
      </Grid>

      <Box mt={4} textAlign="center">
        <Button
          variant="contained"
          color="primary"
          size="large"
          onClick={handleOpenAddDialog}
        >
          + Add Task
        </Button>
      </Box>

      <AddTaskDialog
        open={openAddDialog.isOpen}
        handleClose={handleCloseAddDialog}
        handleAddTask={addTask}
        task={selectedTask}// Опиши связь!!!!!! А то хуй разберешься
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
