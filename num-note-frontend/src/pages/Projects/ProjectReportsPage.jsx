import React, { useEffect, useState, useContext } from "react";
import { useTranslation } from "react-i18next";
import {
    Box,
    Typography,
    List,
    ListItem,
    ListItemText,
    IconButton,
    Collapse,
    Button,
    Tooltip,
    Divider,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Snackbar,
    Fade,
    Grow,
} from "@mui/material";
import {
    ExpandMore,
    ExpandLess,
    Download as DownloadIcon,
    Description as ReportIcon,
    AddCircleOutline as AddIcon,
} from "@mui/icons-material";
import DeleteIcon from "@mui/icons-material/Delete";
import {
    getMyProjects,
    getAttachmentsByProject,
    getProjectReportById,
    downloadAttachment,
    deleteAttachment,
} from "../../services/api";
import Header from "../../components/Header/Header";
import { ThemeContext } from "../../ThemeContext";

const ProjectReportsPage = () => {
    const { t } = useTranslation();
    const { darkMode } = useContext(ThemeContext);

    const [projects, setProjects] = useState([]);
    const [openProjectIds, setOpenProjectIds] = useState([]);
    const [reportsByProject, setReportsByProject] = useState({});
    const [selectedProjectId, setSelectedProjectId] = useState(null);
    const [selectedReportId, setSelectedReportId] = useState(null);
    const [openConfirmCreateDialog, setOpenConfirmCreateDialog] = useState(false);
    const [openConfirmDeleteDialog, setOpenConfirmDeleteDialog] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarOpen, setSnackbarOpen] = useState(false);

    useEffect(() => {
        getMyProjects().then((res) => setProjects(res.data)).catch(console.error);
    }, []);

    const toggleProject = async (projectId) => {
        setOpenProjectIds((prev) =>
            prev.includes(projectId)
                ? prev.filter((id) => id !== projectId)
                : [...prev, projectId]
        );

        if (!reportsByProject[projectId]) {
            try {
                const response = await getAttachmentsByProject(projectId);
                setReportsByProject((prev) => ({ ...prev, [projectId]: response.data }));
            } catch (e) {
                console.error("Ошибка при загрузке отчетов:", e);
            }
        }
    };

    const handleDeleteReport = async () => {
        try {
            await deleteAttachment(selectedReportId); // используй уже имеющийся API метод
            setReportsByProject((prev) => ({
                ...prev,
                [selectedProjectId]: prev[selectedProjectId].filter((r) => r.id !== selectedReportId),
            }));
        } catch (err) {
            console.error("Ошибка при удалении отчета:", err);
            alert("Не удалось удалить отчет.");
        } finally {
            setOpenConfirmDeleteDialog(false);
            setSelectedReportId(null);
            setSelectedProjectId(null);
        }
    };

    const handleCreateReport = async () => {
        try {
            await getProjectReportById(selectedProjectId);
            setSnackbarMessage("📊 Отчет запрошен. Загляните сюда через пару минут.");
            setSnackbarOpen(true);
        } catch (error) {
            console.error("Ошибка при создании отчета:", error);
            setSnackbarMessage("Ошибка при создании отчета.");
            setSnackbarOpen(true);
        } finally {
            setOpenConfirmCreateDialog(false);
            setSelectedProjectId(null);
        }
    };

    const handleDownloadFile = async (id, filename) => {
        try {
            const res = await downloadAttachment(id);
            const blob = new Blob([res.data], {
                type: res.headers["content-type"] || "application/octet-stream",
            });

            const link = document.createElement("a");
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            link.click();
        } catch (err) {
            console.error("Ошибка при скачивании файла:", err);
            alert("Ошибка при скачивании отчета");
        }
    };

    const handleOpenConfirmCreateDialog = (projectId) => {
        setSelectedProjectId(projectId);
        setOpenConfirmCreateDialog(true);
    };
    const handleOpenConfirmDeleteDialog = (projectId, reportId) => {
        setSelectedReportId(reportId);
        setSelectedProjectId(projectId);
        setOpenConfirmDeleteDialog(true);
    };
    return (
        <Box className={`main-content ${darkMode ? "night" : "day"}`}>
            <Header />
            <Box sx={{ maxWidth: "900px", mx: "auto", mt: 4, px: 2 }}>
                <Typography variant="h4" gutterBottom className={`main-title ${darkMode ? "night" : "day"}`}>
                    📁 Отчеты по проектам
                </Typography>

                <Divider sx={{ mb: 3 }} />

                <List>
                    {projects.map((project) => (
                        <Box
                            key={project.id}
                            sx={{
                                border: "1px solid",
                                borderColor: darkMode ? "#3f3f51" : "#ccc",
                                borderRadius: 2,
                                mb: 2,
                                backgroundColor: darkMode ? "rgba(255, 255, 255, 0.1)" : "rgba(255, 255, 255, 0.2)",
                            }}
                        >
                            <ListItem button onClick={() => toggleProject(project.id)}>
                                <ListItemText
                                    primary={
                                        <Typography fontWeight="bold" sx={{ color: darkMode ? " #00f6ff" : "rgb(251, 41, 146)", }}>
                                            {project.icon || "📌"} {project.title}
                                        </Typography>
                                    }
                                    secondary={`Проект ID: ${project.id} — Статус: ${project.status}`}
                                />
                                {openProjectIds.includes(project.id) ? <ExpandLess /> : <ExpandMore />}
                            </ListItem>

                            <Collapse in={openProjectIds.includes(project.id)} timeout="auto" unmountOnExit >
                                <Box sx={{ p: 2, backgroundColor: darkMode ? "rgba(255, 255, 255, 0.1)" : "rgba(255, 255, 255, 0.2)", }}>
                                    <Button
                                        variant="contained"
                                        startIcon={<AddIcon />}
                                        onClick={() => handleOpenConfirmCreateDialog(project.id)}
                                        sx={{
                                            mb: 2,
                                            backgroundColor: "#9932CC",
                                            "&:hover": {
                                                backgroundColor: "#7A25A8",
                                            }
                                        }}
                                    >
                                        Создать новый отчет
                                    </Button>

                                    {reportsByProject[project.id]?.length > 0 ? (
                                        <List dense >
                                            {reportsByProject[project.id].map((report) => (
                                                <Grow in key={report.id} >
                                                    <ListItem
                                                        secondaryAction={
                                                            <Box>
                                                                <Tooltip title="Скачать">
                                                                    <IconButton
                                                                        edge="end"
                                                                        onClick={() => handleDownloadFile(report.id, report.filename)}
                                                                    >
                                                                        <DownloadIcon />
                                                                    </IconButton>
                                                                </Tooltip>

                                                                <Tooltip title="Удалить">
                                                                    <IconButton
                                                                        edge="end"
                                                                        color="error"
                                                                        onClick={() => handleOpenConfirmDeleteDialog(project.id, report.id)}
                                                                    >
                                                                        <DeleteIcon />
                                                                    </IconButton>
                                                                </Tooltip>
                                                            </Box>
                                                        }
                                                    >
                                                        <ReportIcon color="action" sx={{ mr: 1 }} />
                                                        <ListItemText sx={{ color: darkMode ? "#BB86FC" : "primary.main" }} primary={report.filename} />
                                                    </ListItem>
                                                </Grow>
                                            ))}
                                        </List>
                                    ) : (
                                        <Typography variant="body2" color="textSecondary">
                                            Отчеты пока не загружены.
                                        </Typography>
                                    )}
                                </Box>
                            </Collapse>
                        </Box>
                    ))}
                </List>

                {/* Подтверждение создания */}
                <Dialog
                    PaperProps={{
                        sx: {
                            background: darkMode
                                ? "linear-gradient(300deg, #1c1c3c, #2b2b60)"
                                : "linear-gradient(to top left, #d16ba5, #c777b9, #ba83ca, #aa8fd8, #9a9ae1, #8aa7ec, #79b3f4, #69bff8);",
                            color: darkMode ? "#00f6ff" : "#d81b60",
                            boxShadow: darkMode
                                ? "0 0 6px #00f6ff, 0 0 24px #00f6ff"
                                : "0 0 6px #ff90e8, 0 0 24px #ff90e8",
                            borderRadius: 3,

                            px: 2,
                            py: 1,
                            opacity: darkMode ? "0.9" : "0.8"
                        },
                    }}
                    open={openConfirmCreateDialog}
                    onClose={() => setOpenConfirmCreateDialog(false)}
                >
                    <DialogTitle sx={{ fontWeight: "bold", textShadow: darkMode ? "0 0 8px #00f6ff" : "0 0 8px #ff69b4" }}>
                        Создание отчета
                    </DialogTitle>
                    <DialogContent>
                        <Typography>
                            Вы уверены, что хотите создать аналитический PDF-отчет по выбранному проекту?
                        </Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button sx={{

                            color: darkMode ? "#ff4d4d" : "#f44336",

                        }} onClick={() => setOpenConfirmCreateDialog(false)}>Отмена</Button>
                        <Button
                            onClick={handleCreateReport}
                            variant="contained"
                            sx={{
                                backgroundColor: darkMode ? "#00f6ff" : "#ff69b4",
                                color: "#000",
                                "&:hover": {
                                    backgroundColor: darkMode ? "#00e6e6" : "#ff90e8",
                                },
                            }}
                        >
                            Подтвердить
                        </Button>
                    </DialogActions>
                </Dialog>

                {/* Подтверждение удаления */}
                <Dialog
                    PaperProps={{
                        sx: {
                            background: darkMode
                                ? "linear-gradient(300deg, #1c1c3c, #2b2b60)"
                                : "linear-gradient(to top left, #d16ba5, #c777b9, #ba83ca, #aa8fd8, #9a9ae1, #8aa7ec, #79b3f4, #69bff8);",
                            color: darkMode ? "#00f6ff" : "#d81b60",
                            boxShadow: darkMode
                                ? "0 0 6px #00f6ff, 0 0 24px #00f6ff"
                                : "0 0 6px #ff90e8, 0 0 24px #ff90e8",
                            borderRadius: 3,

                            px: 2,
                            py: 1,
                            opacity: darkMode ? "0.9" : "0.8"
                        },
                    }}
                    open={openConfirmDeleteDialog}
                    onClose={() => setOpenConfirmDeleteDialog(false)}
                >
                    <DialogTitle sx={{ fontWeight: "bold", textShadow: darkMode ? "0 0 8px #ff4d4d" : "0 0 8px #f06292" }}>
                        Удаление отчета
                    </DialogTitle>
                    <DialogContent>
                        <Typography>
                            Вы уверены, что хотите удалить аналитический PDF-отчет по выбранному проекту?
                        </Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button sx={{

                            color: darkMode ? "#ff4d4d" : "#f44336",

                        }} onClick={() => setOpenConfirmDeleteDialog(false)} >Отмена</Button>
                        <Button
                            onClick={handleDeleteReport}
                            variant="contained"
                            sx={{
                                backgroundColor: darkMode ? "#ff4d4d" : "#f44336",
                                color: "#000",
                                "&:hover": {
                                    backgroundColor: darkMode ? "#ff6666" : "#e57373",
                                },
                            }}
                        >
                            Подтвердить
                        </Button>
                    </DialogActions>
                </Dialog>
                <Snackbar
                    open={snackbarOpen}
                    autoHideDuration={5000}
                    onClose={() => setSnackbarOpen(false)}
                    message={snackbarMessage}
                    sx={{opacity: darkMode ? "0.9" : "0.9"}}
                    ContentProps={{
                        sx: {
                            backgroundColor: darkMode ? "#2b2b60" : "#fce4ec",
                            color: darkMode ? "#00f6ff" : "#d81b60",
                            border: `1px solid ${darkMode ? "#00f6ff" : "#ff69b4"}`,
                            boxShadow: darkMode
                                ? "0 0 8px #00f6ff"
                                : "0 0 8px #ff69b4",
                            fontWeight: 500,
                            textAlign: "center",
                    
                        },
                    }}
                />

            </Box>
        </Box>
    );
};

export default ProjectReportsPage;
