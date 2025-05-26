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
    const [isPopoverHovered, setIsPopoverHovered] = useState(false);


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
            // Закрываем только если пользователь не навёлся на сам поповер
            //if (isPopoverHovered) {
            handlePopoverClose();
            //}
        }, 20);
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
            sx={{
                px: 4,
                py: 3,


            }}
        >
            <Header />
            <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
                <Typography variant="h4" fontWeight="bold" display="flex" alignItems="center" gap={1} className={`main-title ${darkMode ? "night" : "day"}`}>
                    <CalendarIcon fontSize="large" /> Календарь задач
                </Typography>
                <Box>
                    <IconButton onClick={handlePrevMonth}><PrevIcon /></IconButton>
                    <Typography variant="h6" component="span" mx={2} sx={{ color: darkMode ? " #00f6ff" : "rgb(251, 41, 146)", textShadow: darkMode ? "0 0 6px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px rgb(199, 50, 182), 0 0 24pxrgb(199, 48, 136)", }} >
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
                                        ? theme.palette.mode === "dark"
                                            ? "rgba(0, 246, 255, 0.1)"
                                            : "rgba(255, 105, 180, 0.15)"
                                        : isCurrentMonth
                                            ? (theme.palette.mode === "dark"
                                                ? "rgba(255, 255, 255, 0.1)"
                                                : "rgba(255, 255, 255, 0.3)")
                                            : (theme.palette.mode === "dark"
                                                ? "rgba(255, 255, 255, 0.3)"
                                                : "rgba(255, 255, 255, 0.5)"),
                                    borderRadius: 2,
                                    border: isToday(date)
                                        ? `2px solid ${theme.palette.mode === "dark" ? "#00f6ff" : "#ff69b4"}`
                                        : "1px solid #999",
                                    color: isCurrentMonth
                                        ? "inherit"
                                        : theme.palette.mode === "dark"
                                            ? "rgba(255, 255, 255, 0.44)"
                                            : "rgba(0, 0, 0, 0.36)",
                                    cursor: hasTasks ? "pointer" : "default",
                                    position: "relative",
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "center",
                                    transition: "box-shadow 0.3s",
                                    "&:hover": {
                                        boxShadow: hasTasks
                                            ? `0 0 12px ${theme.palette.mode === "dark" ? "#00f6ff55" : "#ff90e855"}`
                                            : undefined,
                                    },
                                    ///

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
                disableScrollLock
                transitionDuration={0} // 👈 Убирает анимацию
                PaperProps={{
                    onMouseEnter: () => {
                        clearTimeout(closeTimeout);
                        setIsPopoverHovered(true);
                    },
                    onMouseLeave: () => {
                        setIsPopoverHovered(false);
                        handlePopoverDelayedClose();
                    },
                    sx: {
                        p: 2,
                        borderRadius: 2,
                        background: darkMode
                            ? "linear-gradient(300deg, rgba(28,28,60,0.95), rgba(43,43,96,0.95))"
                            : "linear-gradient(to top left, rgba(209,107,165,0.95), rgba(199,119,185,0.9), rgba(186,131,202,0.9), rgba(154,154,225,0.9), rgba(121,179,244,0.9), rgba(105,191,248,0.95))",

                        color: darkMode ? "#00f6ff" : "#ff69b4",
                        boxShadow: darkMode
                            ? "0 0 10px #00f6ff"
                            : "0 0 10px #ff90e8",
                    }
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
                                        borderRadius: "8px",
                                        fontWeight: 500,
                                        fontSize: "0.75rem",

                                        "&:hover": {
                                            opacity: 0.9,
                                            transform: "scale(1.03)",
                                            transition: "transform 0.2s ease",
                                            boxShadow: `0 0 8px ${task.color}`,
                                            backgroundColor: theme.palette.mode === "dark"
                                                ? " #00f6ff"
                                                : "rgb(241, 122, 213)",
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
