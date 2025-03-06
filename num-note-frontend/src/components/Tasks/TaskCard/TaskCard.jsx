import React from "react";
import { Card, CardContent, Typography, Chip, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import "./TaskCard.css";

const TaskCard = ({ task, tags, statuses, onClick }) => {
  const { title, end_time, tag, completed, end_date } = task;
  const { t, i18n } = useTranslation();



  // Функция для получения цвета статуса
  const getStatusColor = (task_status) => {
    const status = statuses.find(status => status.name === task_status);
    return status ? status.color : "#c0c0c0"; // Цвет по умолчанию
  };

  // Функция для получения цвета тега
  const getTagColor = (task_tag) => {
    const tag = tags.find(tag => tag.name === task_tag);
    return tag ? tag.color : "#c0c0c0"; // Цвет по умолчанию
  };

  return (
    <Card
      className={`task-card`}
      onClick={onClick}
      sx={{
        borderLeft: `5px solid ${getTagColor(tag)}`, // Добавляем динамическое окрашивание левого бордера
        paddingLeft: "10px", // Немного отступим для лучшего отображения
      }}
    >
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">{title}</Typography>
          <Chip
            label={task.status} // Отображаем состояние
            sx={{ backgroundColor: getStatusColor(task.status) }}
          />
        </Box>
        <Typography variant="body2" color="textSecondary">
        {t("end_date_n_time")} {end_date} {end_time}
        </Typography>
        <Typography variant="body2" color="textSecondary">
        {t("tag")}: {tag}
        </Typography>
      </CardContent>
    </Card>
  );
};

export default TaskCard;
