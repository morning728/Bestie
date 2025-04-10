import React, { useState, useEffect, useContext } from "react";
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
  Switch,
} from "@mui/material";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { ThemeContext } from "../../../ThemeContext";
import Autocomplete from "@mui/material/Autocomplete";
import "./AddTaskDialog.css";
import { useProjectAccess } from "../../../context/ProjectAccessContext";

const AddTaskDialog = ({ open, handleClose, handleAddTask, task, isEditing, tags, statuses, members }) => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();
  const defaultDate = format(new Date(), "yyyy-MM-dd");

  const { me, myRole, hasPermission, loading } = useProjectAccess();

  const canEditTasks = hasPermission("CAN_EDIT_TASKS") || !isEditing;
  const canManageStatuses = hasPermission("CAN_MANAGE_TASK_STATUSES") || !isEditing;
  const canManageTags = hasPermission("CAN_MANAGE_TASK_TAGS") || !isEditing;
  const canManageReminders = hasPermission("CAN_MANAGE_REMINDERS") || !isEditing;
  const canManageAssignees = hasPermission("CAN_MANAGE_ASSIGNEES") || !isEditing;

  const [newTask, setNewTask] = useState({
    title: "",
    description: "",
    startDate: defaultDate,
    endDate: defaultDate,
    startTime: "00:00",
    endTime: "23:59",
    tagIds: [],
    assigneeIds: [],
    statusId: "",
    priority: "Medium",
    reminder: false,
    reminderDate: "",
    reminderTime: "",
  });

  useEffect(() => {
    if (open) {
      if (task && isEditing) {
        setNewTask({
          ...task,
          tagIds: task.tags?.map((tag) => tag.id) || [],
          assigneeIds: task.assignees?.map((assignee) => assignee.userId) || [],
          reminder: task.reminderDate != null && task.reminderTime != null,
          reminderDate: task.reminderDate || "",
          reminderTime: task.reminderTime || "",
        });
      } else {
        setNewTask({
          title: "",
          description: "",
          startDate: defaultDate,
          endDate: defaultDate,
          startTime: "00:00",
          endTime: "23:59",
          tagIds: [],
          assigneeIds: [],
          statusId: "",
          priority: "Medium",
          reminder: false,
          reminderDate: "",
          reminderTime: "",
        });
      }
    }
  }, [open, task, isEditing, defaultDate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewTask((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleDateChange = (name, date) => {
    const formatted = date ? format(date, "yyyy-MM-dd") : "";
    setNewTask((prev) => ({
      ...prev,
      [name]: formatted,
    }));
  };

  const handleReminderToggle = () => {
    setNewTask((prev) => ({
      ...prev,
      reminder: !prev.reminder,
      reminderDate: !prev.reminder ? defaultDate : "",
      reminderTime: !prev.reminder ? "12:00" : "",
    }));
  };

  const handleTagChange = (e) => {
    setNewTask((prev) => ({
      ...prev,
      tagIds: [parseInt(e.target.value)],
    }));
  };

  const handleSave = () => {
    if (newTask.reminder && (!newTask.reminderDate || !newTask.reminderTime)) {
      alert("Please select both a date and time for the reminder.");
      return;
    }

    const taskToSend = {
      ...newTask,
      reminderDate: newTask.reminder ? newTask.reminderDate : null,
      reminderTime: newTask.reminder ? newTask.reminderTime : null,
    };

    handleAddTask(taskToSend);
    handleClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} className="add-task-dialog">
      <DialogTitle sx={{ backgroundColor: darkMode ? "#2b2b60" : "white", color: darkMode ? "white" : "black" }}>
        {isEditing ? t("edit_task") : t("add_new_task")}
      </DialogTitle>
      <DialogContent sx={{ backgroundColor: darkMode ? "#2b2b60" : "white", color: darkMode ? "white" : "black" }} className="dialog-content">
        <TextField
          disabled={!canEditTasks}
          name="title"
          label={t("task_title")}
          fullWidth
          margin="normal"
          value={newTask.title}
          onChange={handleChange}
          required
        />

        {/* Start */}
        <Box className="date-time-container">
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              disabled={!canEditTasks}
              label={t("start_date")}
              value={newTask.startDate}
              onChange={(date) => handleDateChange("startDate", date)}
              renderInput={(params) => <TextField {...params} fullWidth />}
            />
          </LocalizationProvider>
          <TextField
            disabled={!canEditTasks}
            name="startTime"
            label={t("start_time")}
            type="time"
            fullWidth
            value={newTask.startTime}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
          />
        </Box>

        {/* End */}
        <Box className="date-time-container">
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              disabled={!canEditTasks}
              label={t("end_date")}
              value={newTask.endDate}
              onChange={(date) => handleDateChange("endDate", date)}
              renderInput={(params) => <TextField {...params} fullWidth />}
            />
          </LocalizationProvider>
          <TextField
            disabled={!canEditTasks}
            name="endTime"
            label={t("end_time")}
            type="time"
            fullWidth
            value={newTask.endTime}
            onChange={handleChange}
            InputLabelProps={{ shrink: true }}
          />
        </Box>

        {/* Tag (выбор только одного пока) */}
        <Autocomplete
          disabled={!canManageTags}
          multiple
          options={tags}
          getOptionLabel={(option) => option.name}
          value={tags.filter((tag) => newTask.tagIds.includes(tag.id))}
          onChange={(event, newValue) =>
            setNewTask((prev) => ({
              ...prev,
              tagIds: newValue.map((tag) => tag.id),
            }))
          }
          renderInput={(params) => (
            <TextField {...params} label={t("tag")} margin="normal" fullWidth />
          )}
        />

        <Autocomplete //tyt
          disabled={!canManageAssignees}
          multiple
          options={members}
          getOptionLabel={(option) => option.username}
          value={members.filter((member) => newTask.assigneeIds.includes(member.userId))}
          onChange={(event, newValue) =>
            setNewTask((prev) => ({
              ...prev,
              assigneeIds: newValue.map((assignee) => assignee.userId),
            }))
          }
          renderInput={(params) => (
            <TextField {...params} label={t("assignee")} margin="normal" fullWidth />
          )}
        />

        <TextField
          disabled={!canEditTasks}
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
          disabled={!canEditTasks}
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

        {/* <TextField
        disabled = {!canManageStatuses}
          select
          name="statusId"
          label={t("status")}
          fullWidth
          margin="normal"
          value={newTask.statusId}
          onChange={handleChange}
        >
          {statuses.map((status) => (
            <MenuItem key={status.id} value={status.id}>
              {status.name}
            </MenuItem>
          ))}
        </TextField> */}


        <Autocomplete
          disabled={!canManageStatuses}
          name="statusId"
          getOptionLabel={(option) => option.name}
          options={statuses}
          value={statuses.find((status) => newTask.statusId === status.id) || null}
          onChange={(event, newValue) =>
            setNewTask((prev) => ({
              ...prev,
              statusId: newValue ? newValue.id : null,
            }))
          }
          renderInput={(params) => (
            <TextField {...params} label={t("status")} margin="normal" fullWidth />
          )}
        />

        {/* Reminder */}
        <Box mt={2}>
          <FormControlLabel
            disabled={!canManageReminders}
            control={<Switch checked={newTask.reminder} onChange={handleReminderToggle} />}
            label={t("enable_reminder")}
          />
        </Box>

        {newTask.reminder && (
          <Box className="date-time-container">
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DatePicker
                disabled={!canManageReminders}
                label={t("reminder_date")}
                value={newTask.reminderDate}
                onChange={(date) => handleDateChange("reminderDate", date)}
                renderInput={(params) => <TextField {...params} fullWidth />}
              />
            </LocalizationProvider>
            <TextField
              disabled={!canManageReminders}
              name="reminderTime"
              label={t("reminder_time")}
              type="time"
              fullWidth
              value={newTask.reminderTime}
              onChange={handleChange}
              InputLabelProps={{ shrink: true }}
            />
          </Box>
        )}
      </DialogContent>
      <DialogActions sx={{ backgroundColor: darkMode ? "#2b2b60" : "white", color: darkMode ? "white" : "black" }}>
        <Button onClick={handleClose}>{t("cancel")}</Button>
        <Button onClick={handleSave} color="primary" variant="contained" disabled={!canEditTasks}>
          {t("save")}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddTaskDialog;
