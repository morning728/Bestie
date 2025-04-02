import React, { useContext } from "react";
import KanbanCard from "./KanbanCard";
import "./KanbanBoard.css";
import { ThemeContext } from "../../ThemeContext";

const KanbanColumn = ({ status, tasks, onCardClick }) => {
  const { darkMode } = useContext(ThemeContext);
  return (
    <div className={`kanban-column ${darkMode ? "night" : "day"}`}>
      <div className="kanban-column-header truncate-text" style={{ backgroundColor: status.color }}>
        <strong>{status.name}</strong>
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
