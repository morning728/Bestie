import React from "react";
import { Card, CardContent, Typography, Chip, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import "./TaskCard.css";

const TaskCard = ({ task, tags: allTags, statuses, onClick }) => {
  const { t } = useTranslation();

  const taskTags = task.tags || [];
  const status = statuses.find(s => s.id === task.statusId);
  const statusName = status?.name || t("unknown_status");
  const statusColor = status?.color || "#c0c0c0";

  return (
    <Card
      className="task-card"
      onClick={onClick}
      sx={{
        borderLeft: `5px solid ${taskTags[0]?.color || "#c0c0c0"}`,
        paddingLeft: "10px",
        opacity: task.isArchived ? 0.6 : 1,
      }}
    >
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">{task.title}</Typography>
          <Chip label={statusName} sx={{ backgroundColor: statusColor, color: "#fff" }} />
        </Box>

        <Typography variant="body2" color="textSecondary">
          {t("end_date_n_time")}: {task.endDate} {task.endTime?.slice(0, 5)}
        </Typography>

        {taskTags.length > 0 && (
          <Box mt={1} display="flex" gap={1} flexWrap="wrap">
            {taskTags.map((tag) => (
              <Chip
                key={tag.id}
                label={tag.name}
                size="small"
                sx={{ backgroundColor: `#${tag.color}`, color: "#000" }}
              />
            ))}
          </Box>
        )}
      </CardContent>
    </Card>
  );
};

export default TaskCard;
