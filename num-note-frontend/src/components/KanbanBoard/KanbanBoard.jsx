import React, { useRef, useContext, useState, useEffect } from "react";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import KanbanColumn from "./KanbanColumn";
import "./KanbanBoard.css";
import { updateProjectStatus } from "../../services/api"; // если не импортировал
import { ThemeContext } from "../../ThemeContext";

const KanbanBoard = ({ tasks, statuses, onCardClick, onStatusChange }) => {
    const [localStatuses, setLocalStatuses] = useState([]);

    useEffect(() => {
        setLocalStatuses([...statuses]);
    }, [statuses]);
    const sortedStatuses = [...localStatuses].sort((a, b) => a.position - b.position);

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


    const moveColumn = async (indexA, indexB) => {
        const sorted = [...localStatuses].sort((a, b) => a.position - b.position);
        const statusA = sorted[indexA];
        const statusB = sorted[indexB];

        const newStatusA = { ...statusA, position: statusB.position };
        const newStatusB = { ...statusB, position: statusA.position };

        try {
            await Promise.all([
                updateProjectStatus(statusA.projectId, newStatusA),
                updateProjectStatus(statusB.projectId, newStatusB),
            ]);

            // теперь в localStatuses нужно обновить позиции
            setLocalStatuses((prev) =>
                prev.map((status) => {
                    if (status.id === statusA.id) return newStatusA;
                    if (status.id === statusB.id) return newStatusB;
                    return status;
                })
            );
        } catch (error) {
            console.error("Ошибка при изменении порядка колонок:", error);
        }
    };



    const moveColumnLeft = (index) => {
        if (index > 0) moveColumn(index, index - 1);
    };

    const moveColumnRight = (index) => {
        if (index < sortedStatuses.length - 1) moveColumn(index, index + 1);
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
                {sortedStatuses
                    .map((status, index) => (
                        <Droppable droppableId={String(status.id)} key={status.id}>
                            {(provided) => (
                                    <KanbanColumn
                                        key={status.id}
                                        status={status}
                                        tasks={tasks.filter((task) => task.statusId === status.id).sort((a, b) => {
                                            const dateDiff = new Date(a.startDate) - new Date(b.startDate);
                                            if (dateDiff !== 0) return dateDiff;
                                            return a.title.localeCompare(b.title); // замените title на нужное поле с названием
                                        })}
                                        onCardClick={onCardClick}
                                        onMoveLeft={() => moveColumnLeft(index)}
                                        onMoveRight={() => moveColumnRight(index)}
                                        isFirst={index === 0}
                                        isLast={index === localStatuses.length - 1}
                                        droppableProvided={provided}
                                    />
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
