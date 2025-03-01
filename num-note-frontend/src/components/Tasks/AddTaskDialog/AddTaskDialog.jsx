import React, { useState, useEffect } from "react";
import { format } from "date-fns";
import { useTranslation } from "react-i18next";
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
import { useContext } from "react";
import { ThemeContext } from "../../../ThemeContext";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import "./AddTaskDialog.css"; // Подключаем CSS

const AddTaskDialog = ({ open, handleClose, handleAddTask, task, isEditing }) => {
  const { darkMode } = useContext(ThemeContext);
  const defaultDate = format(new Date(), "yyyy-MM-dd");
  const { t, i18n } = useTranslation();

  const [newTask, setNewTask] = useState(
    task || {
      title: "",
      start_time: "00:00",
      end_time: "23:59",
      start_date: defaultDate,
      end_date: defaultDate,
      tag: "",
      description: "",
      priority: "Low",
      status: "Pending",
      reminder: false,
      reminder_date: "",
      reminder_time: "",
    }
  );

  useEffect(() => {
    if (task && task != null) {
      setNewTask(task);
    } else {
      setNewTask({
        title: "",
        start_time: "00:00",
        end_time: "23:59",
        start_date: defaultDate,
        end_date: defaultDate,
        tag: "",
        description: "",
        priority: "Low",
        status: "Pending",
        reminder: false,
        reminder_date: "",
        reminder_time: "",
      });
    }
  }, [task, defaultDate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewTask((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleDateChange = (name, date) => {
    const formattedDate = date ? format(new Date(date), "yyyy-MM-dd") : "";
    setNewTask((prev) => ({
      ...prev,
      [name]: formattedDate,
    }));
  };

  const handleReminderToggle = () => {
    setNewTask((prev) => ({
      ...prev,
      reminder: !prev.reminder,
      reminder_date: !prev.reminder ? defaultDate : "", // При включении ставим текущую дату
      reminder_time: !prev.reminder ? "12:00" : "", // Дефолтное время 12:00
    }));
  };

  const handleSave = () => {
    if (newTask.reminder && (!newTask.reminder_date || !newTask.reminder_time)) {
      alert("Please select both a date and time for the reminder.");
      return;
    }
    handleAddTask(newTask);
    handleClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} className="add-task-dialog">
      <DialogTitle
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>{isEditing ? t("edit_task") : t("add_new_task")}</DialogTitle>
      <DialogContent
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>
        <TextField
          name="title"
          label={t("task_title")}
          fullWidth
          margin="normal"
          value={newTask.title}
          onChange={handleChange}
          required
        />

        {/* Start Date & Time */}
        <Box className="date-time-container">
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label={t("start_date")}
              value={newTask.start_date}
              onChange={(date) => handleDateChange("start_date", date)}
              renderInput={(params) => <TextField {...params} fullWidth />}
            />
          </LocalizationProvider>
          <TextField
            name="start_time"
            label={t("start_time")}
            type="time"
            fullWidth
            value={newTask.start_time}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
          />
        </Box>

        {/* End Date & Time */}
        <Box className="date-time-container">
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label={t("end_date")}
              value={newTask.end_date}
              onChange={(date) => handleDateChange("end_date", date)}
              renderInput={(params) => <TextField {...params} fullWidth />}
            />
          </LocalizationProvider>
          <TextField
            name="end_time"
            label={t("end_time")}
            type="time"
            fullWidth
            value={newTask.end_time}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
          />
        </Box>

        <TextField
          select
          name="tag"
          label={t("tag")}
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
          label={t("description")}
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
          label={t("priority")}
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
          label={t("status")}
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

        <Box mt={2}>
          <FormControlLabel
            control={<Switch checked={newTask.reminder} onChange={handleReminderToggle} />}
            label={t("enable_reminder")}
          />
        </Box>

        {/* Поля даты и времени для напоминания (появляются при включении) */}
        {newTask.reminder && (
          <Box className="date-time-container">
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DatePicker
                label={t("reminder_date")}
                value={newTask.reminder_date}
                onChange={(date) => handleDateChange("reminder_date", date)}
                renderInput={(params) => <TextField {...params} fullWidth />}
              />
            </LocalizationProvider>
            <TextField
              name="reminder_time"
              label={t("reminder_time")}
              type="time"
              fullWidth
              value={newTask.reminder_time}
              onChange={handleChange}
              InputLabelProps={{ shrink: true }}
            />
          </Box>
        )}
      </DialogContent>
      <DialogActions
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>
        <Button onClick={handleClose}>Cancel</Button>
        <Button onClick={handleSave} color="primary" variant="contained">
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddTaskDialog;
