import React from "react";
import { useTranslation } from "react-i18next";
import { Dialog, Box, Typography, Chip, Button } from "@mui/material";
import { useContext } from "react";
import { ThemeContext } from "../../../ThemeContext";
import "./TaskDetailsDialog.css";
import { useProjectAccess } from "../../../context/ProjectAccessContext";

const TaskDetailsDialog = ({ open, task, handleClose, onEdit, onArchive, onRestore, statuses }) => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();
  const { me, myRole, hasPermission, loading } = useProjectAccess();
  const canEditTasks = hasPermission("CAN_EDIT_TASKS");
  const canArchive = hasPermission("CAN_ARCHIVE_TASKS");
  const canRestore = hasPermission("CAN_RESTORE_TASKS");

  if (!task) return null;

  const status = statuses?.find((s) => s.id === task.statusId);

  return (
    <Dialog open={open}
      onClose={handleClose}
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
      }}>
      <Box
        sx={{
          backgroundColor: "transparent",
          boxShadow: "none",              // лишние свечения убрать
          color: darkMode ? "#00f6ff" : "#fff",
          textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12pxrgb(220, 75, 191), 0 0 24px #ff90e8",
          padding: "24px",
          borderRadius: "16px"
        }}
        className={`task-details-dialog ${darkMode ? "night" : "day"}`}
      >
        <Typography variant="h5" gutterBottom className={`task-title ${darkMode ? "night" : "day"}`} >
          {task.title}
        </Typography>

        <Typography variant="body2"  sx={{ color: darkMode ? "#00f6ff" : "#d81b60" }} gutterBottom>
          {t("start_date")}: {task.startDate} & {task.startTime}
        </Typography>

        <Typography variant="body2"  sx={{ color: darkMode ? "#00f6ff" : "#d81b60" }} gutterBottom>
          {t("end_date_n_time")}: {task.endDate} & {task.endTime}
        </Typography>

        <Typography variant="body2"  sx={{ color: darkMode ? "#00f6ff" : "#d81b60" }} gutterBottom>
          {t("status")}: {status?.name ?? t("no_status")}
        </Typography>

        <Typography variant="body2"  sx={{ color: darkMode ? "#00f6ff" : "#d81b60" }} gutterBottom>
          {t("tags")}: {task.tags && task.tags.length > 0 ? task.tags.map(tag => tag.name).join(", ") : t("no_tags")}
        </Typography>

        <Typography variant="body2"  sx={{ color: darkMode ? "#00f6ff" : "#d81b60" }} gutterBottom>
          {t("priority")}: {task.priority}
        </Typography>

        <Typography variant="body2"  sx={{ color: darkMode ? "#00f6ff" : "#d81b60" }} gutterBottom>
          {t("reminder")}: {task.reminderDate && task.reminderTime ? `${task.reminderDate} @ ${task.reminderTime}` : t("off")}
        </Typography>

        <Typography variant="body1" mt={2}>
          {task.description}
        </Typography>

        <Box mt={3} display="flex" justifyContent="space-around">
          {!task.isArchived && (<Button
            disabled={!canEditTasks}
            variant="contained"
            color="primary"
            onClick={() => onEdit(task)}
            sx={{
              maxWidth: "150px",       // ограничение по ширине
              overflow: "hidden",      // скрыть выходящий текст
              textOverflow: "ellipsis",// показать "..."
              whiteSpace: "nowrap",     // не переносить на новую строку
            }}
          >
            {t("edit_short")}
          </Button>)
          }
          <Button
            disabled={task.isArchived ? !canRestore : !canArchive}
            variant="contained"
            onClick={() =>
              task.isArchived ? onRestore(task.id) : onArchive(task.id)
            }
          >
            {task.isArchived ? t("restore") : t("in_archive")}
          </Button>
          <Button variant="outlined" onClick={handleClose}>
            {t("close")}
          </Button>
        </Box>
      </Box>
    </Dialog>
  );
};

export default TaskDetailsDialog;
