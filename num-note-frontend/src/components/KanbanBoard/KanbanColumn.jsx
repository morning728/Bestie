import React, { useContext } from "react";
import KanbanCard from "./KanbanCard";
import "./KanbanBoard.css";
import { Box, Typography } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import IconButton from "@mui/material/IconButton";

// В компонент KanbanColumn добавим новые пропсы
const KanbanColumn = ({ status, tasks, onCardClick, onMoveLeft, onMoveRight, isFirst, isLast }) => {
  const { darkMode } = useContext(ThemeContext);

  return (
    <div className={`kanban-column ${darkMode ? "night" : "day"}`}>
      <div className="kanban-column-header truncate-text" style={{ backgroundColor: status.color }}>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 1,
            px: 1,
            width: "100%",
          }}
        >
          <IconButton size="small" onClick={onMoveLeft} disabled={isFirst}>
            <ArrowBackIcon fontSize="small" />
          </IconButton>

          <Typography variant="subtitle2" sx={{ flexGrow: 1, textAlign: "center", fontWeight: "bold" }}>
            {status.name}
          </Typography>

          <IconButton size="small" onClick={onMoveRight} disabled={isLast}>
            <ArrowForwardIcon fontSize="small" />
          </IconButton>
        </Box>
      </div>
      <div className="kanban-column-body">
        {tasks.map((task, index) => (
          <KanbanCard key={task.id} task={task} index={index} onClick={() => onCardClick(task)} />
        ))}
      </div>
    </div>
  );
};


export default KanbanColumn;