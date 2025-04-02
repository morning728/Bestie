import React, { useState, useEffect, useContext } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  IconButton,
  Grid,
  Paper,
  Popover,
} from "@mui/material";
import { SketchPicker } from "react-color";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import AddIcon from "@mui/icons-material/Add";
import SaveIcon from "@mui/icons-material/Save";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import {
  getProjectStatuses,
  addProjectStatus,
  updateProjectStatus,
  deleteProjectStatus,
} from "../../../services/api";
import "../ProjectSettingsPage.css";

const StatusesTab = ({ projectId }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);

  const [statuses, setStatuses] = useState([]);
  const [newStatusName, setNewStatusName] = useState("");
  const [newStatusColor, setNewStatusColor] = useState("#9932CC");
  const [editingStatus, setEditingStatus] = useState(null);
  const [editStatusName, setEditStatusName] = useState("");
  const [editStatusColor, setEditStatusColor] = useState("");

  // Для popover выбора цвета
  const [colorAnchor, setColorAnchor] = useState(null);
  const [editColorAnchor, setEditColorAnchor] = useState(null);

  useEffect(() => {
    fetchStatuses();
  }, [projectId]);

  const fetchStatuses = () => {
    getProjectStatuses(projectId)
      .then((res) => setStatuses(res.data))
      .catch((err) => console.error("Ошибка загрузки статусов:", err));
  };

  // Добавление нового статуса
  const handleAddStatus = () => {
    if (newStatusName.trim()) {
      addProjectStatus(projectId, {
        name: newStatusName,
        color: newStatusColor,
      })
        .then(() => {
          setNewStatusName("");
          setNewStatusColor("#9932CC");
          fetchStatuses();
        })
        .catch((err) => console.error("Ошибка добавления статуса:", err));
    }
  };

  // Удаление статуса
  const handleDeleteStatus = (statusId) => {
    deleteProjectStatus(projectId, statusId)
      .then(fetchStatuses)
      .catch((err) => console.error("Ошибка удаления статуса:", err));
  };

  // Редактирование статуса
  const handleEditStatus = (status) => {
    setEditingStatus(status.id);
    setEditStatusName(status.name);
    setEditStatusColor(status.color);
  };

  // Сохранение изменений статуса
  const handleSaveStatus = () => {
    updateProjectStatus(projectId, {
      id: editingStatus,
      name: editStatusName,
      color: editStatusColor,
      projectId: projectId,
    })
      .then(() => {
        setEditingStatus(null);
        fetchStatuses();
      })
      .catch((err) => console.error("Ошибка обновления статуса:", err));
  };

  return (
    <Box className={darkMode ? "settings-tab-content dark" : "settings-tab"} sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        {t("statuses")}
      </Typography>

      {/* Добавление нового статуса */}
      <Paper sx={{ p: 3, mb: 3, display: "flex", alignItems: "center", gap: 2 }}>
        <TextField
          fullWidth
          label={t("new_status")}
          value={newStatusName}
          onChange={(e) => setNewStatusName(e.target.value)}
        />
        <Box
          sx={{
            width: 40,
            height: 40,
            borderRadius: "8px",
            background: `linear-gradient(135deg, ${newStatusColor}, #ffffff)`,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            border: "1px solid #ccc",
            cursor: "pointer",
            transition: "0.3s",
            "&:hover": { opacity: 0.8 },
          }}
          onClick={(e) => setColorAnchor(e.currentTarget)}
        >
          🎨
        </Box>
        <Popover
          open={Boolean(colorAnchor)}
          anchorEl={colorAnchor}
          onClose={() => setColorAnchor(null)}
          anchorOrigin={{
            vertical: "bottom",
            horizontal: "left",
          }}
        >
          <SketchPicker
            color={newStatusColor}
            onChangeComplete={(color) => {
              setNewStatusColor(color.hex);
              setColorAnchor(null);
            }}
          />
        </Popover>
        <Button
          variant="contained"
          color="primary"
          onClick={handleAddStatus}
          startIcon={<AddIcon />}
          sx={{ whiteSpace: "nowrap" }}
        >
          {t("add")}
        </Button>
      </Paper>

      {/* Отображение существующих статусов */}
      <Box>
        <Typography variant="subtitle1" sx={{ mb: 2 }}>
          {t("existing_statuses_СТАТУСЫ ОТОБРАЖАЮТСЯ НА КАНБАН ДОСКЕ В ПОРЯДКЕ ИХ ДОБАВЛЕНИЯ")}
        </Typography>
        <Grid container spacing={2}>
          {statuses.length > 0 ? (
            statuses.map((status) => (
              <Grid item xs={12} sm={6} md={4} key={status.id}>
                <Paper
                  sx={{
                    p: 2,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    backgroundColor: status.color,
                    color: "#fff",
                    borderRadius: "8px",
                    transition: "0.3s",
                    "&:hover": { opacity: 0.9 },
                  }}
                >
                  {editingStatus === status.id ? (
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                      <TextField
                        value={editStatusName}
                        onChange={(e) => setEditStatusName(e.target.value)}
                        size="small"
                        sx={{ backgroundColor: "#fff", borderRadius: "4px" }}
                      />
                      <Box
                        sx={{
                          width: 40,
                          height: 40,
                          borderRadius: "8px",
                          background: `linear-gradient(135deg, ${editStatusColor}, #ffffff)`,
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                          border: "1px solid #ccc",
                          cursor: "pointer",
                          transition: "0.3s",
                          "&:hover": { opacity: 0.8 },
                        }}
                        onClick={(e) => setEditColorAnchor(e.currentTarget)}
                      >
                        🎨
                      </Box>
                      <Popover
                        open={Boolean(editColorAnchor)}
                        anchorEl={editColorAnchor}
                        onClose={() => setEditColorAnchor(null)}
                        anchorOrigin={{
                          vertical: "bottom",
                          horizontal: "left",
                        }}
                      >
                        <SketchPicker
                          color={editStatusColor}
                          onChangeComplete={(color) => {
                            setEditStatusColor(color.hex);
                            setEditColorAnchor(null);
                          }}
                        />
                      </Popover>
                      <IconButton onClick={handleSaveStatus} color="success">
                        <SaveIcon />
                      </IconButton>
                    </Box>
                  ) : (
                    <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                      <Typography variant="subtitle1">{status.name}</Typography>
                      <IconButton onClick={() => handleEditStatus(status)} color="default">
                        <EditIcon />
                      </IconButton>
                    </Box>
                  )}
                  <IconButton onClick={() => handleDeleteStatus(status.id)} color="error">
                    <DeleteIcon />
                  </IconButton>
                </Paper>
              </Grid>
            ))
          ) : (
            <Typography variant="body2" color="textSecondary">
              {t("no_statuses_available")}
            </Typography>
          )}
        </Grid>
      </Box>
    </Box>
  );
};

export default StatusesTab;
