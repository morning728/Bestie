import React, { useContext } from "react";
import "./KanbanBoard.css";
import { Draggable } from "react-beautiful-dnd";
import { ThemeContext } from "../../ThemeContext";

const KanbanCard = ({ task, index, onClick }) => {
  const { darkMode } = useContext(ThemeContext);
  return (
    <Draggable draggableId={String(task.id)} index={index}>
      {(provided) => (
        <div
          className={`kanban-card ${darkMode ? "night" : "day"}`}
          onClick={() => onClick(task)}
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
        >
          <h3 className="truncate-text">{task.title}</h3>
          <p>{task.startDate} – {task.endDate}</p>
          <div className="kanban-tags">
            {task.tags?.map((tag) => (
              <span key={tag.id} className="kanban-tag" style={{ backgroundColor: tag.color }}>
                {tag.name}
              </span>
            ))}
          </div>
        </div>
      )}
    </Draggable>
  );
};

export default KanbanCard;
