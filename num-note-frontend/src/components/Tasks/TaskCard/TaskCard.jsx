import { React, useContext } from "react";
import { Card, CardContent, Typography, Chip, Box } from "@mui/material";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import "./TaskCard.css";

const TaskCard = ({ task, tags: allTags, statuses, onClick }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);

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
        backgroundColor: darkMode ? "rgba(255, 255, 255, 0.1)" : "rgba(255, 255, 255, 0.2)",
        boxShadow: darkMode
          ? "0 0 8px rgba(0, 246, 255, 0.3)"
          : "0 0 8px rgba(255, 105, 180, 0.3), 0 0 8px rgba(121, 179, 244, 0.4)",
        transition: "all 0.3s ease",
        paddingLeft: "10px",
        color: "inherit",
        "&:hover": {
          boxShadow: darkMode
            ? "0 0 12px rgba(0, 246, 255, 0.8)"
            : "0 0 12px 4px rgba(255, 105, 180, 0.3), 0 0 20px 8px rgba(121, 179, 244, 0.4)",
          transform: "scale(1.02)",
          color: "inherit"
        }
      }}
    >
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">{task.title}</Typography>
          {status && (<Chip label={statusName} sx={{ backgroundColor: statusColor, color: "#fff" }} />)}
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
