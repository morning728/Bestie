import React from "react";
import { Dialog, Box, Typography, Chip, Button } from "@mui/material";
import "./TaskDetailsDialog.css";

const TaskDetailsDialog = ({ open, task, handleClose, onEdit, onDelete }) => {
  if (!task) return null;



  return (
    <Dialog open={open} onClose={handleClose}>
      <Box className="task-details-dialog">
        <Typography variant="h5" gutterBottom>
          {task.title}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          Time: {task.time}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          Category: {task.tag}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          Priority: {task.priority}
        </Typography>
        <Typography variant="body2" color="textSecondary" gutterBottom>
          Reminder: {task.reminder ? "Yes" : "No"}
        </Typography>
        <Typography variant="body1" mt={2}>
          {task.description}
        </Typography>

        <Box mt={3} display="flex" justifyContent="space-around">
          <Button variant="contained" color="primary" onClick={() => onEdit(task)}>
            Edit
          </Button>
          <Button variant="contained" color="secondary" onClick={() => onDelete(task.id)}>
            Delete
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
