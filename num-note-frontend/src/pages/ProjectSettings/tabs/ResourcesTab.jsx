import React, { useState, useEffect, useContext, useRef } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  IconButton,
  Grid,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import CloseIcon from "@mui/icons-material/Close";
import AddIcon from "@mui/icons-material/Add";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import {
  getProjectResources,
  addProjectResource,
  updateProjectResource,
  deleteProjectResource,
} from "../../../services/api";
import "../ProjectSettingsPage.css";
import { useProjectAccess } from "../../../context/ProjectAccessContext.js";

const ResourcesTab = ({ projectId }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);

  const { me, hasPermission, loading } = useProjectAccess();
  const can = hasPermission("CAN_EDIT_PROJECT");

  const [resources, setResources] = useState([]);
  const [newResource, setNewResource] = useState({ url: "", description: "" });
  const [editingResource, setEditingResource] = useState(null);
  const [editResource, setEditResource] = useState({ url: "", description: "" });

  const inputRef = useRef(null);

  useEffect(() => {
    fetchResources();
  }, [projectId]);

  const fetchResources = () => {
    getProjectResources(projectId)
      .then((res) => setResources(res.data))
      .catch((err) => console.error("Ошибка загрузки ресурсов:", err));
  };

  // Добавление нового ресурса
  const handleAddResource = () => {
    if (newResource.url.trim()) {
      addProjectResource(projectId, newResource)
        .then(() => {
          setNewResource({ url: "", description: "" });
          fetchResources();
        })
        .catch((err) => console.error("Ошибка добавления ресурса:", err));
    }
  };

  // Удаление ресурса
  const handleDeleteResource = (resourceId) => {
    deleteProjectResource(projectId, resourceId)
      .then(fetchResources)
      .catch((err) => console.error("Ошибка удаления ресурса:", err));
  };

  // Начало редактирования ресурса
  const startEditingResource = (resource) => {
    setEditingResource(resource.id);
    setEditResource({ url: resource.url, description: resource.description, projectId: projectId });

    // Ждём ререндер и ставим фокус на поле
    setTimeout(() => {
      if (inputRef.current) inputRef.current.focus();
    }, 100);
  };

  // Сохранение изменений
  const handleSaveResource = (resourceId) => {
    updateProjectResource(projectId, { id: resourceId, ...editResource })
      .then(() => {
        setEditingResource(null);
        fetchResources();
      })
      .catch((err) => console.error("Ошибка обновления ресурса:", err));
  };

  // Отмена редактирования
  const handleCancelEdit = () => {
    setEditingResource(null);
    setEditResource({ url: "", description: "" });
  };

  return (
    <Box className={darkMode ? "settings-tab-content dark" : "settings-tab"}>
      <Typography variant="h6" gutterBottom>{t("resources")}</Typography>

      {/* Добавление нового ресурса */}
      <Box className="resource-input">
        <TextField
          fullWidth
          label={t("resource_url")}
          value={newResource.url}
          onChange={(e) => setNewResource({ ...newResource, url: e.target.value })}
          sx={{ mb: 2 }}
          disabled={!can}
        />
        <TextField
          fullWidth
          label={t("description")}
          value={newResource.description}
          onChange={(e) => setNewResource({ ...newResource, description: e.target.value })}
          sx={{ mb: 2 }}
          disabled={!can}
        />
        <Button
          variant="contained"
          sx={{ backgroundColor: "#9932CC", color: "#fff", fontWeight: "bold" }}
          onClick={handleAddResource}
          startIcon={<AddIcon />}
          disabled={!can}
        >
          {t("add")}
        </Button>
      </Box>

      {/* Список ресурсов */}
      <Box mt={3}>
        <Typography variant="subtitle1">{t("existing_resources")}</Typography>
        <Grid container spacing={2}>
          {resources.length > 0 ? (
            resources.map((resource) => (
              <Grid item xs={12} key={resource.id}>
                <Paper
                  className="resource-item"
                  sx={{
                    display: "flex",
                    alignItems: "center",
                    padding: 2,
                    background: editingResource === resource.id
                      ? darkMode ? "#3A3D5B" : "#B15FC8"
                      : darkMode ? "#2B2D42" : "#9932CC",
                    color: "#fff",
                    borderRadius: "12px",
                    transition: "all 0.3s ease",
                    "&:hover": {
                      transform: "scale(1.02)",
                    },
                  }}
                >
                  {editingResource === resource.id ? (
                    <Box sx={{ display: "flex", flexGrow: 1, alignItems: "center", gap: 2 }}>
                      <TextField
                      disabled={!can}
                        fullWidth
                        value={editResource.url}
                        onChange={(e) => setEditResource({ ...editResource, url: e.target.value })}
                        size="small"
                        sx={{ backgroundColor: "#fff", borderRadius: 1 }}
                        inputRef={inputRef}
                      />
                      <TextField
                      disabled={!can}
                        fullWidth
                        value={editResource.description}
                        onChange={(e) => setEditResource({ ...editResource, description: e.target.value })}
                        size="small"
                        sx={{ backgroundColor: "#fff", borderRadius: 1 }}
                      />
                      <IconButton color="success" onClick={() => handleSaveResource(resource.id)} disabled={!can}>
                        <SaveIcon />
                      </IconButton>
                      <IconButton color="error" onClick={handleCancelEdit} disabled={!can}>
                        <CloseIcon />
                      </IconButton>
                    </Box>
                  ) : (
                    <Box sx={{ flexGrow: 1, display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                      <Box>
                        <Typography variant="body1" sx={{ fontWeight: "bold", color: "#fff" }}>
                          {resource.description || "Без описания"}
                        </Typography>
                        <Typography variant="body2" sx={{ color: "#ddd" }}>
                          <a href={resource.url} target="_blank" rel="noopener noreferrer" style={{ color: "#00BFFF" }}>
                            {resource.url}
                          </a>
                        </Typography>
                      </Box>
                      <Box sx={{ display: "flex", gap: 1 }}>
                        <IconButton sx={{backgroundcolor:"rgba(77, 20, 65, 0.91)" }} onClick={() => startEditingResource(resource)}  disabled={!can}>
                          <EditIcon />
                        </IconButton>
                        <IconButton color="error" onClick={() => handleDeleteResource(resource.id)}  disabled={!can}>
                          <DeleteIcon />
                        </IconButton>
                      </Box>
                    </Box>
                  )}
                </Paper>
              </Grid>
            ))
          ) : (
            <Typography variant="body2" color="textSecondary">
              {t("no_resources_available")}
            </Typography>
          )}
        </Grid>
      </Box>
    </Box>
  );
};

export default ResourcesTab;
