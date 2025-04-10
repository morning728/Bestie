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
    <Dialog open={open} onClose={handleClose}>
      <Box
        className="task-details-dialog"
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}
      >
        <Typography variant="h5" gutterBottom>
          {task.title}
        </Typography>

        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("start_date")}: {task.startDate} & {task.startTime}
        </Typography>

        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("end_date_n_time")}: {task.endDate} & {task.endTime}
        </Typography>

        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("status")}: {status?.name ?? t("no_status")}
        </Typography>

        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("tags")}: {task.tags && task.tags.length > 0 ? task.tags.map(tag => tag.name).join(", ") : t("no_tags")}
        </Typography>

        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("priority")}: {task.priority}
        </Typography>

        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("reminder")}: {task.reminderDate && task.reminderTime ? `${task.reminderDate} @ ${task.reminderTime}` : t("off")}
        </Typography>

        <Typography variant="body1" mt={2}>
          {task.description}
        </Typography>

        <Box mt={3} display="flex" justifyContent="space-around">
          {!task.isArchived && (<Button
          disabled = {!canEditTasks}
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
          disabled = {task.isArchived ? !canRestore : !canArchive}
            variant="contained"
            color="secondary"
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
