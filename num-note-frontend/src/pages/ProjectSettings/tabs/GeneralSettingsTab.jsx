import React, { useState } from "react";
import { Box, TextField, Button, MenuItem, Typography } from "@mui/material";
import { SketchPicker } from "react-color";
import { useTranslation } from "react-i18next";
import "./GeneralSettingsTab.css";
import { updateProject } from "../../../services/api";

const priorities = ["Low", "Medium", "High", "Critical"];
const statuses = ["Planned", "In Progress", "Completed", "On Hold"];
const icons = ["📁", "🚀", "📊", "⚙️", "📝", "🎯", "🛠️"];

const GeneralSettingsTab = ({ project }) => {
  const { t } = useTranslation();

  const [title, setTitle] = useState(project.title);
  const [description, setDescription] = useState(project.description);
  const [color, setColor] = useState(project.color);
  const [icon, setIcon] = useState(project.icon);
  const [priority, setPriority] = useState(project.priority);
  const [status, setStatus] = useState(project.status);

  const handleSave = () => {
    const updatedProject = {
      title,
      description,
      color,
      icon,
      priority,
      status,
    };

    updateProject(project.id, updatedProject)
      .then(() => alert(t("saved_successfully")))
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
      />
      <TextField
        label={t("description")}
        fullWidth
        multiline
        rows={3}
        margin="normal"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <Box className="settings-row">
        <TextField
          select
          label={t("priority")}
          value={priority}
          onChange={(e) => setPriority(e.target.value)}
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
        >
          {icons.map((i) => (
            <MenuItem key={i} value={i}>{i}</MenuItem>
          ))}
        </TextField>
      </Box>
      <Box className="color-picker-section">
        <Typography>{t("project_color")}</Typography>
        <SketchPicker color={color} onChange={(c) => setColor(c.hex)} />
      </Box>

      <Button
        variant="contained"
        color="primary"
        className="save-btn"
        onClick={handleSave}
      >
        {t("save_changes")}
      </Button>
    </Box>
  );
};

export default GeneralSettingsTab;
