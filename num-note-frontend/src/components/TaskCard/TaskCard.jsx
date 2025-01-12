import React from "react";
import { Card, CardContent, Typography, Chip, Box } from "@mui/material";
import "./TaskCard.css";

const TaskCard = ({ task, onClick }) => {
  const { title, time, tag, completed } = task;

  // Функция для выбора цвета
  const getChipColor = (status) => {
    switch (status) {
      case "Completed":
        return "success"; // Зеленый
      case "In Progress":
        return "primary"; // Синий
      case "Pending":
        return "warning"; // Желтый
      case "Overdue":
        return "error"; // Красный
      default:
        return "default"; // Серый
    }
  };

  return (
    <Card className={`task-card ${completed ? "completed" : "pending"}`} onClick={onClick}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">{title}</Typography>
          <Chip
            label={task.status} // Отображаемое состояние
            color={getChipColor(task.status)} // Вызываем функцию для определения цвета
          />
        </Box>
        <Typography variant="body2" color="textSecondary">
          Time: {time}
        </Typography>
        <Typography variant="body2" color="textSecondary">
          Category: {tag}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default TaskCard;
