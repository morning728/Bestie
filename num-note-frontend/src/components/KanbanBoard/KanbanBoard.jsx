import React, { useRef, useContext, useState } from "react";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import KanbanColumn from "./KanbanColumn";
import "./KanbanBoard.css";
import { ThemeContext } from "../../ThemeContext";

const KanbanBoard = ({ tasks, statuses, onCardClick, onStatusChange }) => {
    //DragDrop
    const [isDragging, setIsDragging] = useState(false);

    const handleDragStart = () => {
        setIsDragging(true);
      };

    const handleDragEnd = (result) => {
        setIsDragging(false); // возвращаем скролл после окончания
        const { source, destination, draggableId } = result;

        if (!destination || source.droppableId === destination.droppableId) return;

        const taskId = parseInt(draggableId);
        const newStatusId = parseInt(destination.droppableId);

        onStatusChange(taskId, newStatusId); // вызов API или обновления
    };
    //DragDrop
    const { darkMode } = useContext(ThemeContext);
    const boardRef = useRef(null);
    let isDown = false;
    let startX;
    let scrollLeft;

    const handleMouseDown = (e) => {
        isDown = true;
        boardRef.current.classList.add("dragging");
        startX = e.pageX - boardRef.current.offsetLeft;
        scrollLeft = boardRef.current.scrollLeft;
    };

    const handleMouseLeave = () => {
        isDown = false;
        boardRef.current.classList.remove("dragging");
    };

    const handleMouseUp = () => {
        isDown = false;
        boardRef.current.classList.remove("dragging");
    };

    const handleMouseMove = (e) => {
        if (!isDown || isDragging) return;
        e.preventDefault();
        const x = e.pageX - boardRef.current.offsetLeft;
        const walk = (x - startX) * 1.5; // чувствительность
        boardRef.current.scrollLeft = scrollLeft - walk;
    };
    return (
        <DragDropContext onDragEnd={handleDragEnd} onDragStart={handleDragStart}>
            <div
                ref={boardRef}
                className={`kanban-board ${darkMode ? "night" : "day"}`}
                onMouseDown={handleMouseDown}
                onMouseLeave={handleMouseLeave}
                onMouseUp={handleMouseUp}
                onMouseMove={handleMouseMove}
            >
                {statuses.map((status) => (
                    <Droppable droppableId={String(status.id)} key={status.id}>
                        {(provided) => (
                            <div
                                className="kanban-column"
                                ref={provided.innerRef}
                                {...provided.droppableProps}
                            >
                                <KanbanColumn
                                    key={status.id}
                                    status={status}
                                    tasks={tasks.filter((task) => task.statusId === status.id)}
                                    onCardClick={onCardClick}
                                />
                                {provided.placeholder}
                            </div>
                        )}
                    </Droppable>
                ))}
                {/* Колонка для задач без статуса */}
                {tasks.some((task) => task.statusId == null) && (
                    <Droppable droppableId="null">
                        {(provided) => (
                            <div className="kanban-column" ref={provided.innerRef} {...provided.droppableProps}>
                                <KanbanColumn
                                    key="no-status"
                                    status={{ id: "no-status", name: "Без статуса", color: "#999999" }}
                                    tasks={tasks.filter((task) => task.statusId == null)}
                                    onCardClick={onCardClick}
                                />
                                {provided.placeholder}
                            </div>
                        )}
                    </Droppable>
                )}
            </div>
        </DragDropContext>
    );
};

export default KanbanBoard;
