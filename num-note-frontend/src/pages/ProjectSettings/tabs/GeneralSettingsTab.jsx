import React, { useState } from "react";
import { Box, TextField, Button, MenuItem, Typography } from "@mui/material";
import { SketchPicker } from "react-color";
import { useTranslation } from "react-i18next";
import "./GeneralSettingsTab.css";
import { useProjectsContext } from "../../../context/ProjectsContext";
import { useProjectAccess } from "../../../context/ProjectAccessContext.js";

const priorities = ["Low", "Medium", "High", "Critical"];
const statuses = ["Planned", "In Progress", "Completed", "On Hold"];
const icons = ["📁", "🚀", "📊", "⚙️", "📝", "🎯", "🛠️"];

const GeneralSettingsTab = ({ project }) => {
  const { t } = useTranslation();

  const { me, hasPermission, loading } = useProjectAccess();
  const can = hasPermission("CAN_EDIT_PROJECT");

  const [title, setTitle] = useState(project.title);
  const [description, setDescription] = useState(project.description);
  const [color, setColor] = useState(project.color);
  const [icon, setIcon] = useState(project.icon);
  const [priority, setPriority] = useState(project.priority);
  const [status, setStatus] = useState(project.status);
  const { editProject, fetchProjects } = useProjectsContext(); // 👈 из контекста

  const handleSave = () => {
    const updatedProject = {
      title,
      description,
      color,
      icon,
      priority,
      status,
    };

    editProject(project.id, updatedProject) // 👈 обновляем в useProjects
      .then(() => {
        alert(t("saved_successfully"));
      })
      .catch((err) => console.error("Ошибка при обновлении проекта:", err));
  };

  return (
    <Box className="general-settings-tab">
      <TextField
        label={t("project_title")}
        fullWidth
        margin="normal"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        disabled={!can}
      />
      <TextField
        label={t("description")}
        fullWidth
        multiline
        rows={3}
        margin="normal"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        disabled={!can}
      />
      <Box className="settings-row">
        <TextField
          select
          label={t("priority")}
          value={priority}
          onChange={(e) => setPriority(e.target.value)}
          disabled={!can}
        >
          {priorities.map((p) => (
            <MenuItem key={p} value={p}>{p}</MenuItem>
          ))}
        </TextField>
        <TextField
          select
          label={t("status")}
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          disabled={!can}
        >
          {statuses.map((s) => (
            <MenuItem key={s} value={s}>{s}</MenuItem>
          ))}
        </TextField>
        <TextField
          select
          label={t("icon")}
          value={icon}
          onChange={(e) => setIcon(e.target.value)}
          disabled={!can}
        >
          {icons.map((i) => (
            <MenuItem key={i} value={i}>{i}</MenuItem>
          ))}
        </TextField>
      </Box>
      <Box className="color-picker-section">
        <Typography>{t("project_color")}</Typography>
        <SketchPicker disabled={!can} color={color} onChange={(c) => setColor(c.hex)} />
      </Box>

      <Button
        variant="contained"
        color="primary"
        className="save-btn"
        onClick={handleSave}
        disabled={!can}
      >
        {t("save_changes")}
      </Button>
    </Box>
  );
};

export default GeneralSettingsTab;
