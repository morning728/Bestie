import React, { useState, useContext, useEffect } from "react";
import {
    Box,
    Typography,
    IconButton,
    Popover,
    Chip,
    useTheme,
} from "@mui/material";
import {
    ArrowBackIosNew as PrevIcon,
    ArrowForwardIos as NextIcon,
    CalendarMonth as CalendarIcon,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import dayjs from "dayjs";
import "dayjs/locale/ru";
import { ThemeContext } from "../../ThemeContext";
import { useTranslation } from "react-i18next";
import Header from "../../components/Header/Header";
import { useProjectsContext } from "../../context/ProjectsContext";
import { getMyTasksByPeriod } from "../../services/api";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
dayjs.extend(isSameOrBefore);

dayjs.locale("ru");

const CalendarPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { darkMode } = useContext(ThemeContext);
    const theme = useTheme();
    const today = dayjs();
    const [currentMonth, setCurrentMonth] = useState(today.startOf("month"));
    const [hoverDateKey, setHoverDateKey] = useState(null);
    const [hoverPosition, setHoverPosition] = useState({ top: 0, left: 0 });
    const [anchorEl, setAnchorEl] = useState(null);

    const [closeTimeout, setCloseTimeout] = useState(null);
    const [calendarTasks, setCalendarTasks] = useState({});

    const { projects } = useProjectsContext();

    const startDate = currentMonth.startOf("week");
    const endDate = currentMonth.endOf("month").endOf("week");

    const weeks = [];
    let day = startDate.clone();
    while (day.isBefore(endDate)) {
        weeks.push(
            Array(7)
                .fill(null)
                .map(() => {
                    const thisDay = day;
                    day = day.add(1, "day");
                    return thisDay;
                })
        );
    }

    useEffect(() => {
        const start = currentMonth.startOf("month").startOf("week");
        const end = currentMonth.endOf("month").endOf("week");

        getMyTasksByPeriod(start.format("YYYY-MM-DD"), end.format("YYYY-MM-DD"))
            .then((res) => {
                const grouped = {};

                res.data.forEach((task) => {
                    const taskStart = dayjs(task.startDate);
                    const taskEnd = dayjs(task.endDate);
                    for (
                        let d = taskStart;
                        d.isSameOrBefore(taskEnd);
                        d = d.add(1, "day")
                    ) {
                        const key = d.format("YYYY-MM-DD");
                        if (!grouped[key]) grouped[key] = [];

                        const project = projects.find((p) => p.id === task.projectId);
                        if (project) {
                            const existing = grouped[key].find(
                                (t) => t.project === project.title
                            );
                            if (existing) {
                                existing.count += 1;
                            } else {
                                grouped[key].push({
                                    project: project.title,
                                    color: project.color || "#9932CC",
                                    count: 1,
                                    projectId: project.id, // 👈 добавляем
                                });
                            }
                        }
                    }
                });

                setCalendarTasks(grouped);
            })
            .catch((err) => {
                console.error("Ошибка загрузки задач для календаря:", err);
            });
    }, [currentMonth, projects]);

    const handlePrevMonth = () =>
        setCurrentMonth((prev) => prev.subtract(1, "month"));
    const handleNextMonth = () =>
        setCurrentMonth((prev) => prev.add(1, "month"));

    const handlePopoverOpen = (event, date) => {
        const key = date.format("YYYY-MM-DD");
        if (hoverDateKey === key) return;

        setAnchorEl(event.currentTarget);
        setHoverDateKey(key);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
        setHoverDateKey(null);
    };

    const handlePopoverDelayedClose = () => {
        const timeout = setTimeout(() => {
            handlePopoverClose();
        }, 350); // увеличена задержка
        setCloseTimeout(timeout);
    };

    const isToday = (date) => date.isSame(today, "day");
    const tasksForDay = (date) => {
        const key = date.format("YYYY-MM-DD");
        return calendarTasks[key] || [];
    };

    return (
        <Box
            className={`main-content ${theme.palette.mode === "dark" ? "night" : "day"}`}
            sx={{ px: 4, py: 3 }}
        >
            <Header />
            <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
                <Typography variant="h4" fontWeight="bold" display="flex" alignItems="center" gap={1}>
                    <CalendarIcon fontSize="large" /> Календарь задач
                </Typography>
                <Box>
                    <IconButton onClick={handlePrevMonth}><PrevIcon /></IconButton>
                    <Typography variant="h6" component="span" mx={2}>
                        {currentMonth.format("MMMM YYYY")}
                    </Typography>
                    <IconButton onClick={handleNextMonth}><NextIcon /></IconButton>
                </Box>
            </Box>

            <Box
                sx={{
                    display: "grid",
                    gridTemplateColumns: "repeat(7, 1fr)",
                    gap: 1,
                    textAlign: "center",
                }}
            >
                {["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"].map((day) => (
                    <Typography key={day} variant="subtitle2" fontWeight="bold">
                        {day}
                    </Typography>
                ))}

                {weeks.map((week, wi) =>
                    week.map((date, di) => {
                        const isCurrentMonth = date.month() === currentMonth.month();
                        const dayKey = date.format("YYYY-MM-DD");
                        const tasksToday = calendarTasks[dayKey] || [];
                        const hasTasks = tasksToday.length > 0;

                        return (
                            <Box
                                key={`${wi}-${di}`}
                                onPointerEnter={(e) => hasTasks && handlePopoverOpen(e, date)}
                                onPointerLeave={() => hasTasks && handlePopoverDelayedClose()}
                                sx={{
                                    p: 1,
                                    minHeight: 80,
                                    backgroundColor: isToday(date)
                                        ? "#9932CC20"
                                        : isCurrentMonth
                                            ? theme.palette.background.paper
                                            : "#f0f0f0",
                                    borderRadius: 2,
                                    border: isToday(date) ? "2px solid #9932CC" : "1px solid #ccc",
                                    color: isCurrentMonth ? "inherit" : "#aaa",
                                    cursor: hasTasks ? "pointer" : "default",
                                    position: "relative",
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "center",
                                    "&:hover": {
                                        boxShadow: hasTasks ? "0 0 0 2px #9932CC33" : undefined,
                                    },
                                }}
                            >
                                <Typography fontWeight="bold">{date.date()}</Typography>

                                {hasTasks && (
                                    <Box
                                        sx={{
                                            position: "absolute",
                                            bottom: 6,
                                            left: "50%",
                                            transform: "translateX(-50%)",
                                            display: "flex",
                                            gap: "3px",
                                            alignItems: "center",
                                        }}
                                    >
                                        {tasksToday.slice(0, 3).map((task, idx) => (
                                            <Box
                                                key={idx}
                                                sx={{
                                                    width: 6,
                                                    height: 6,
                                                    backgroundColor: task.color,
                                                    borderRadius: "50%",
                                                }}
                                            />
                                        ))}
                                        {tasksToday.length > 3 && (
                                            <Typography variant="caption" sx={{ fontSize: "0.6rem", ml: 0.5 }}>
                                                +
                                            </Typography>
                                        )}
                                    </Box>
                                )}
                            </Box>
                        );
                    })
                )}
            </Box>

            <Popover
                open={Boolean(hoverDateKey)}
                anchorEl={anchorEl}
                onClose={handlePopoverClose}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
                transformOrigin={{ vertical: "top", horizontal: "center" }}
                disableEnforceFocus
                disableAutoFocus
                disableRestoreFocus
                PaperProps={{
                    onMouseEnter: () => clearTimeout(closeTimeout),
                    onMouseLeave: handlePopoverDelayedClose,
                    sx: { p: 2, borderRadius: 2, backgroundColor: "#fff" }
                }}
            >
                {hoverDateKey && (
                    <Box>
                        <Typography fontWeight="bold" gutterBottom>
                            {dayjs(hoverDateKey).format("D MMMM")} — Проектов: {calendarTasks[hoverDateKey]?.length || 0}
                        </Typography>
                        <Box sx={{ display: "flex", flexDirection: "column", gap: 1 }}>
                            {(calendarTasks[hoverDateKey] || []).map((task, idx) => (
                                <Chip
                                    key={idx}
                                    label={`${task.project} (${task.count})`}
                                    onClick={() => navigate(`/projects/${task.projectId}/tasks`)}
                                    sx={{
                                        backgroundColor: task.color,
                                        color: "#fff",
                                        cursor: "pointer",
                                        "&:hover": {
                                            opacity: 0.9,
                                        },
                                    }}
                                />
                            ))}
                        </Box>
                    </Box>
                )}
            </Popover>

        </Box>
    );
};

export default CalendarPage;
