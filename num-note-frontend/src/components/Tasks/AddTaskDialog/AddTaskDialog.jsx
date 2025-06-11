import React, { useState, useEffect, useContext } from "react";
import { useDropzone } from "react-dropzone";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import InsertDriveFileIcon from "@mui/icons-material/InsertDriveFile";
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
  Typography
} from "@mui/material";
import { LocalizationProvider, DatePicker } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import { ThemeContext } from "../../../ThemeContext";
import Autocomplete from "@mui/material/Autocomplete";
import "./AddTaskDialog.css";
import DeleteIcon from "@mui/icons-material/Delete";
import DownloadIcon from "@mui/icons-material/Download";
import IconButton from "@mui/material/IconButton";
import { Tooltip } from "@mui/material";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline"
import { uploadAttachment, getAttachmentsByTask, deleteAttachment, downloadAttachment, getReminderByTaskId, decomposeTask } from "../../../services/api"; // не забудь импорт
import { useProjectAccess } from "../../../context/ProjectAccessContext";

const AddTaskDialog = ({ open, handleClose, handleAddTask, handleDecompose, task, isEditing, tags, statuses, members }) => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();
  const defaultDate = format(new Date(), "yyyy-MM-dd");

  const [existingFiles, setExistingFiles] = useState([]); // с сервера
  const [newFiles, setNewFiles] = useState([]); // новые
  const [filesMarkedForDeletion, setFilesMarkedForDeletion] = useState([]);
  const [subtaskCount, setSubtaskCount] = useState(2);



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
    reminderText: "",
  });

  useEffect(() => {
    const init = async () => {
      if (open) {
        if (task && isEditing) {
          let tempTask = {
            ...task,
            tagIds: task.tags?.map((tag) => tag.id) || [],
            assigneeIds: task.assignees?.map((assignee) => assignee.userId) || [],
          };

          const [attachmentsResult, reminderResult] = await Promise.allSettled([
            getAttachmentsByTask(task.id),
            getReminderByTaskId(task.id),
          ]);

          if (attachmentsResult.status === "fulfilled") {
            setExistingFiles(attachmentsResult.value.data);
          } else {
            console.error("Ошибка при загрузке вложений:", attachmentsResult.reason);
            setExistingFiles([]);
          }

          if (reminderResult.status === "fulfilled") {
            const reminder = reminderResult.value.data;
            tempTask = {
              ...tempTask,
              reminder: reminder?.reminder ?? false,
              reminderDate: reminder?.reminderDate ?? "",
              reminderTime: reminder?.reminderTime ?? "",
              reminderText: reminder?.reminderText ?? "",
            };
          } else {
            console.error("Ошибка при загрузке напоминания:", reminderResult.reason);
            tempTask = {
              ...tempTask,
              reminder: false,
              reminderDate: "",
              reminderTime: "",
              reminderText: "",
            };
          }

          setNewTask(tempTask);
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
            reminderText: "",
          });
          setExistingFiles([]);
        }
        setNewFiles([]);
        setFilesMarkedForDeletion([]);
      }
    }
    init();
  }, [open, task, isEditing, defaultDate]);

  const handleRemoveNewFile = (index) => {
    setNewFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleRemoveExistingFile = (id) => {
    setFilesMarkedForDeletion((prev) => [...prev, id]);
    setExistingFiles((prev) => prev.filter((file) => file.id !== id)); // только визуально убираем
  };

  const handleDownloadFile = async (id, fileNameFallback) => {
    try {
      const response = await downloadAttachment(id); // responseType: 'blob'

      const contentDisposition = response.headers['content-disposition'];
      let fileName = fileNameFallback;

      // Пытаемся вытащить имя из заголовка, если оно есть
      if (contentDisposition) {
        const match = contentDisposition.match(/filename="?([^"]+)"?/);
        if (match && match[1]) {
          fileName = decodeURIComponent(match[1]);
        }
      }

      const blob = new Blob([response.data], {
        type: response.headers["content-type"] || "application/octet-stream",
      });

      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = fileName;
      link.click();
    } catch (error) {
      console.error("Ошибка при скачивании файла:", error);
      alert("Не удалось скачать файл.");
    }
  };


  const onDrop = (acceptedFiles) => {
    setNewFiles((prev) => [...prev, ...acceptedFiles]);
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop });


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

  const handleSave = async () => {
    if (newTask.reminder && (!newTask.reminderDate || !newTask.reminderTime)) {
      alert("Please select both a date and time for the reminder.");
      return;
    }

    const taskToSend = {
      ...newTask,
      reminderText: newTask.reminder ? newTask.reminderText : null,
      reminderDate: newTask.reminder ? newTask.reminderDate : null,
      reminderTime: newTask.reminder ? newTask.reminderTime : null,
    };

    try {
      const createdTask = await handleAddTask(taskToSend);

      if (!createdTask?.id) throw new Error("Задача не создана");

      // 1️⃣ Удаление отмеченных файлов
      await Promise.all(filesMarkedForDeletion.map((id) => deleteAttachment(id)));

      // 2️⃣ Загрузка новых
      await Promise.all(
        newFiles.map((file) => uploadAttachment(createdTask.id, file))
      );

      handleClose();
    } catch (error) {
      console.error("Ошибка при сохранении задачи или работе с файлами:", error);
      alert("Произошла ошибка при сохранении задачи.");
    }
  };

  const handleDecomposeTask = async () => {
    const taskToSend = {
      ...newTask,
      reminderText: null,
      reminderDate: null,
      reminderTime: null,
    };

    try {
      const response = await handleDecompose(taskToSend, subtaskCount);
      handleClose();
      alert(`Задачи с секунды на секунду будут добавлены!`);
    } catch (error) {
      if (error.response) {
        alert(`Ошибка при декомпозиции: ${error.response.data}`);
      } else {
        console.log("Ошибка при отправке запроса:", error);
        alert("Сетевая ошибка при декомпозиции задачи.");
      }
    }
  };



  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth="xl"
      fullWidth
      className={`add-task-dialog ${darkMode ? "night" : "day"}`}
      PaperProps={{
        sx: {
          background: darkMode
            ? "linear-gradient(300deg, #1c1c3c, #2b2b60)"
            : "linear-gradient(to top left, #d16ba5, #c777b9, #ba83ca, #aa8fd8, #9a9ae1, #8aa7ec, #79b3f4, #69bff8);",
          color: darkMode ? "#00f6ff" : "#d81b60",
          boxShadow: darkMode
            ? "0 0 6px #00f6ff, 0 0 24px #00f6ff"
            : "0 0 6px #ff90e8, 0 0 24px #ff90e8",
          borderRadius: 3,

          px: 2,
          py: 1,
          opacity: darkMode ? "0.93" : "0.8"
        },
      }}
    >

      <DialogTitle sx={{

        color: darkMode ? "#00f6ff" : "#fff",
        textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px #ff90e8, 0 0 24px #ff90e8"

      }}>
        {isEditing ? t("edit_task") : t("add_new_task")}
      </DialogTitle>
      <DialogContent
        sx={{

          color: darkMode ? "#00f6ff" : "#fff",

          display: "flex",
          flexDirection: "row",
          gap: 4,
          alignItems: "flex-start",
        }}
        className="dialog-content"
      >
        <Box sx={{ flex: 2 }}>
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
              sx={{
                textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px #ff90e8, 0 0 24px #ff90e8"
              }}
              disabled={!canManageReminders}
              control={<Switch checked={newTask.reminder} onChange={handleReminderToggle} />}
              label={t("enable_reminder")}
            />
          </Box>

          {newTask.reminder && (
            <Box mt={2}>
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

              {/* Новое поле: reminderText */}
              <TextField
                disabled={!canManageReminders}
                name="reminderText"
                label={t("reminder_text")}
                fullWidth
                multiline
                rows={3}
                value={newTask.reminderText}
                onChange={handleChange}
                sx={{ mt: 2 }}
              />
            </Box>
          )}


        </Box>
        {/* Правая часть — drag & drop + список файлов */}
        <Box sx={{ width: 320, display: 'flex', flexDirection: 'column', maxHeight: 800, gap: 2, mt: 1 }}>
          {/* Зона загрузки */}
          <Box
            {...getRootProps()}
            sx={{
              border: darkMode ? "2px dashed rgba(0, 246, 255, 0.8)" : "2px dashed rgba(255, 105, 180, 0.8)",
              borderRadius: 2,
              height: 150,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              backgroundColor: isDragActive
                ? darkMode
                  ? "rgba(0, 246, 255, 0.1)"
                  : "rgba(255, 105, 180, 0.1)"
                : darkMode
                  ? "rgba(0, 0, 0, 0.3)"
                  : "rgba(255, 255, 255, 0.3)",
              cursor: "pointer",
              transition: "0.3s ease",
              flexShrink: 0,
              color: darkMode ? "#00f6ff" : "#ff69b4",
              textShadow: darkMode
                ? "0 0 6px rgba(0, 246, 255, 0.7)"
                : "0 0 6px rgba(255, 105, 180, 0.7)",
            }}
          >
            <input {...getInputProps()} />
            <CloudUploadIcon sx={{ fontSize: 48, color: darkMode ? "#00f6ff" : "#ff69b4" }} />
            <Typography mt={1} textAlign="center" fontSize={14}>
              {isDragActive ? t("drop_files_here") : t("drag_and_drop_or_click")}
            </Typography>
          </Box>


          {/* Список файлов */}
          <Box
            sx={{
              flex: 1,
              overflowY: "auto",

              color: darkMode ? "#00f6ff" : "#fff",
              textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px #ff90e8, 0 0 24px #ff90e8",
              borderRadius: 2,
              p: 2,
              minHeight: 0,
            }}
          >
            <Typography variant="subtitle2" mb={1}>
              {t("attached_files")} ({existingFiles.length + newFiles.length})
            </Typography>

            {existingFiles.length === 0 && newFiles.length === 0 ? (
              <Typography variant="body2" color="textSecondary">
                {t("no_files_attached")}
              </Typography>
            ) : (
              <>
                {existingFiles.map((file) => (
                  <Box
                    key={`existing-${file.id}`}
                    display="flex"
                    alignItems="center"
                    mb={1}
                    sx={{

                      color: darkMode ? "#00f6ff" : "#fff",
                      textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px #ff90e8, 0 0 24px #ff90e8",
                      p: 1,
                      borderRadius: 1,
                      boxShadow: 1,
                    }}
                  >
                    <InsertDriveFileIcon sx={{ mr: 1 }} />
                    <Typography
                      variant="body2"
                      sx={{ wordBreak: "break-all", flexGrow: 1 }}
                    >
                      {file.filename}
                    </Typography>
                    <Typography variant="caption" sx={{ mx: 1 }}>
                      {(Number(file.fileSize || 0) / 1024 / 1024).toFixed(1)} MB
                    </Typography>
                    <IconButton onClick={() => handleDownloadFile(file.id, file.filename)}>
                      <DownloadIcon fontSize="small" />
                    </IconButton>
                    <IconButton onClick={() => handleRemoveExistingFile(file.id)}>
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Box>
                ))}

                {newFiles.map((file, idx) => (
                  <Box
                    key={`new-${idx}`}
                    display="flex"
                    alignItems="center"
                    mb={1}
                    sx={{

                      color: darkMode ? "#00f6ff" : "#fff",
                      textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px #ff90e8, 0 0 24px #ff90e8",
                      p: 1,
                      borderRadius: 1,
                      boxShadow: 1,
                    }}
                  >
                    <InsertDriveFileIcon sx={{ mr: 1 }} />
                    <Typography
                      variant="body2"
                      sx={{ wordBreak: "break-all", flexGrow: 1 }}
                    >
                      {file.name}
                    </Typography>
                    <Typography variant="caption" sx={{ mx: 1 }}>
                      {(file.size / 1024 / 1024).toFixed(1)} MB
                    </Typography>
                    <IconButton onClick={() => handleRemoveNewFile(idx)}>
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Box>
                ))}
              </>
            )}
          </Box>


        </Box>


      </DialogContent>




      <DialogActions
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          color: darkMode ? "#00f6ff" : "#fff",

        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <TextField
            label="Подзадач"
            type="number"
            size="small"
            variant="outlined"
            InputProps={{ inputProps: { min: 2, max: 7 } }}
            value={subtaskCount}
            onChange={(e) => setSubtaskCount(Number(e.target.value))}
            sx={{
              width: 100,
              "& .MuiInputBase-input": {
                color: darkMode ? "#00f6ff" : "#fff",
              },
              "& .MuiInputLabel-root": {
                color: darkMode ? "#00f6ff" : "#ff90e8",
              },
              "& .MuiOutlinedInput-root": {
                "& fieldset": {
                  borderColor: darkMode ? "#00f6ff" : "#ff90e8",
                },
              },
            }}
          />

          <Tooltip
            title={
              <Box sx={{ fontSize: 13, maxWidth: 300 }}>
                Метод разбивает задачу на несколько подзадач (от 2 до 7) с помощью GigaChat.
                Ответственные, теги, статус и приоритет переносятся из исходной задачи.
                Новые подзадачи получат индивидуальные названия, описания и периоды выполнения.
                <br />
                <br />
                ⚠️ Функционал находится в стадии тестирования. Возможны неточные или странные формулировки.
              </Box>
            }
            arrow
          >
            <IconButton size="small" sx={{ color: "inherit" }}>
              <HelpOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>

          <Button
            variant="outlined"
            onClick={handleDecomposeTask}
            disabled={subtaskCount < 2 || subtaskCount > 7 || !canEditTasks}
            sx={{
              color: "inherit",
              borderColor: "currentColor",
              textShadow: "inherit",
              "&:hover": {
                backgroundColor: "rgba(255, 255, 255, 0.08)",
              },
            }}
          >
            Разбить задачу
          </Button>
        </Box>

        <Box>
          <Button onClick={handleClose} sx={{marginRight:"1em"}}>{t("cancel")}</Button>
          <Button
            onClick={handleSave}
            color="primary"
            variant="contained"
            disabled={!canEditTasks}
          >
            {t("save")}
          </Button>
        </Box>
      </DialogActions>
    </Dialog>
  );
};

export default AddTaskDialog;
