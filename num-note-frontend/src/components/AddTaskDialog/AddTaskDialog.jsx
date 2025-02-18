import React, { useState, useEffect } from "react";
import { format, parse } from "date-fns";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Button,
  MenuItem,
  Box,
  FormControlLabel,
  Switch
} from "@mui/material";

const AddTaskDialog = ({ open, handleClose, handleAddTask, task, isEditing }) => {
  const [newTask, setNewTask] = useState(
    task || {
      title: "",
      time: "",
      tag: "",
      description: "",
      priority: "Low",
      status: "Pending",
      reminder: false,
      reminderTime: "", 
      start_date: "",
      end_date: "",
    }
  );

  useEffect(() => {
    if (task && task != null) {
      setNewTask(task);
    } else {
      setNewTask({
        title: "",
        time: "",
        tag: "",
        description: "",
        priority: "Low",
        status: "Pending",
        reminder: false,
        reminderTime: "", 
      });
    }
  }, [task]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    // Если поле `time`, проверяем и форматируем значение
    if (name === "time") {
      // Проверяем, правильный ли формат времени
      const parsedTime = parse(value, "HH:mm", new Date());
      if (isNaN(parsedTime)) {
        console.error("Invalid time format");
        return;
      }

      // Обновляем время в формате HH:mm
      setNewTask((prev) => ({
        ...prev,
        [name]: format(parsedTime, "HH:mm"),
      }));
    } else {
      // Для остальных полей просто обновляем значение
      setNewTask((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleReminderToggle = () => {
    setNewTask((prev) => ({
      ...prev,
      reminder: !prev.reminder,
      reminderTime: !prev.reminder ? "" : prev.reminderTime, // Сбрасываем время напоминания, если выключено
    }));
  };

  const handleSave = () => {
    if (newTask.reminder && !newTask.reminderTime) {
      alert("Please select a time for the reminder.");
      return;
    }
    handleAddTask(newTask);
    handleClose();
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>{isEditing ? "Edit Task" : "Add New Task"}</DialogTitle>
      <DialogContent>
        <TextField
          name="title"
          label="Task Title"
          fullWidth
          margin="normal"
          value={newTask.title}
          onChange={handleChange}
          required
        />
        <TextField
          name="time"
          label="Time"
          type="time"
          fullWidth
          margin="normal"
          value={newTask.time} // Устанавливаем пустую строку, если значение отсутствует
          onChange={handleChange}
          InputLabelProps={{
            shrink: true, // Оставляем label видимым
          }}
        />
        <TextField
          select
          name="tag"
          label="Category"
          fullWidth
          margin="normal"
          value={newTask.tag}
          onChange={handleChange}
        >
          <MenuItem value="Work">Work</MenuItem>
          <MenuItem value="Health">Health</MenuItem>
          <MenuItem value="Personal">Personal</MenuItem>
          <MenuItem value="Study">Study</MenuItem>
        </TextField>
        <TextField
          name="description"
          label="Description"
          fullWidth
          multiline
          rows={4}
          margin="normal"
          value={newTask.description}
          onChange={handleChange}
        />
        <TextField
          select
          name="priority"
          label="Priority"
          fullWidth
          margin="normal"
          value={newTask.priority}
          onChange={handleChange}
        >
          <MenuItem value="Low">Low</MenuItem>
          <MenuItem value="Medium">Medium</MenuItem>
          <MenuItem value="High">High</MenuItem>
        </TextField>
        <TextField
          select
          name="status"
          label="Status"
          fullWidth
          margin="normal"
          value={newTask.status}
          onChange={handleChange}
        >
          <MenuItem value="Pending">Pending</MenuItem>
          <MenuItem value="In Progress">In Progress</MenuItem>
          <MenuItem value="Completed">Completed</MenuItem>
          <MenuItem value="Overdue">Overdue</MenuItem>
        </TextField>

        {/* Напоминание */}
        <Box mt={2}>
          <FormControlLabel
            control={<Switch checked={newTask.reminder} onChange={handleReminderToggle} />}
            label="Enable Reminder"
          />
        </Box>
        {newTask.reminder && (
          <TextField
            name="reminderTime"
            label="Reminder Time"
            type="time"
            fullWidth
            margin="normal"
            value={newTask.reminderTime}
            onChange={handleChange}
            inputProps={{
              min: new Date().toISOString().split("T")[1].slice(0, 5), // Ограничение на выбор времени
            }}
            InputLabelProps={{
              shrink: true, // Оставляем label видимым
            }}
          />
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Cancel</Button>
        <Button onClick={handleSave} color="primary" variant="contained">
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddTaskDialog;