import React from "react";
import { useTranslation } from "react-i18next";
import { Dialog, Box, Typography, Chip, Button } from "@mui/material";
import { useContext } from "react";
import { ThemeContext } from "../../../ThemeContext";
import "./TaskDetailsDialog.css";

const TaskDetailsDialog = ({ open, task, handleClose, onEdit, onArchive }) => {
  const { darkMode } = useContext(ThemeContext);
  const { t, i18n } = useTranslation();
  if (!task) return null;

  return (
    <Dialog open={open} onClose={handleClose}>
      <Box className="task-details-dialog"
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>
        <Typography variant="h5" gutterBottom>
          {task.title}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("end_date_n_time")}: {task.end_date} & {task.end_time}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("tag")}: {task.tag}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("priority")}: {task.priority}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          {t("reminder")}: {task.reminder ? t("on") : t("off")}
        </Typography>
        <Typography variant="body1" mt={2}>
          {task.description}
        </Typography>

        <Box mt={3} display="flex" justifyContent="space-around">
          <Button variant="contained" color="primary" onClick={() => onEdit(task)}>
            Edit
          </Button>
          <Button variant="contained" color="secondary" onClick={() => onArchive(task.id)}>
            In Archive
          </Button>
          <Button variant="outlined" onClick={handleClose}>
            Close
          </Button>
        </Box>
      </Box>
    </Dialog>
  );
};

export default TaskDetailsDialog;
