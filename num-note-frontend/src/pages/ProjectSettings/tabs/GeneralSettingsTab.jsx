import React, { useState, useContext } from "react";
import { Box, TextField, Button, MenuItem, Typography } from "@mui/material";
import { SketchPicker } from "react-color";
import { useTranslation } from "react-i18next";
import "./GeneralSettingsTab.css";
import { useProjectsContext } from "../../../context/ProjectsContext";
import { useProjectAccess } from "../../../context/ProjectAccessContext.js";
import { ThemeContext } from "../../../ThemeContext";
const presetColors = [
  "#FF0000", "#DC143C", "#B22222", "#8B0000", "#A52A2A", "#F08080", "#FA8072",
  "#FF6347", "#FF4500", "#FF8C00", "#FFA500", "#FFD700", "#FFC0CB", "#FF69B4",
  "#FF1493", "#DB7093", "#D2691E", "#FFFF00", "#F0E68C", "#ADFF2F", "#E6E6FA",
  "#32CD32", "#00FF00", "#008000", "#2E8B57", "#8FBC8F", "#808000", "#87CEEB",
  "#B0E0E6", "#00FFFF", "#00CED1", "#20B2AA", "#00FA9A", "#7FFFD4", "#40E0D0",
  "#5F9EA0", "#4682B4", "#0000FF", "#6A5ACD", "#4B0082", "#9932CC", "#800080",
  "#BA55D3", "#DA70D6", "#D8BFD8", "#C0C0C0", "#F5DEB3", "#000000"
];
const priorities = ["Low", "Medium", "High", "Critical"];
const statuses = ["Planned", "In Progress", "Completed", "On Hold"];
const icons = ["📁", "🚀", "📊", "⚙️", "📝", "🎯", "🛠️"];

const GeneralSettingsTab = ({ project }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);

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
    <Box className="general-settings-tab" sx={{
          color: darkMode ? "#00f6ff" : "#fff",

        }}>
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
        <Typography sx={{textShadow: darkMode ? "0 0 6px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px rgb(199, 50, 182), 0 0 24pxrgb(199, 48, 136)",}}>{t("project_color")}</Typography>
        <Box
          sx={{
            display: "flex",
            gap: 15,
            padding: 2,
            background: darkMode ? "#2b2b60" : "#b28cd9",
            borderRadius: "16px",
            border: darkMode ? "1px solid #00f6ff" : "1px solid rgb(203, 35, 144)",
            
            color: darkMode ? "#00f6ff" : "#fff",
            maxWidth: "100%"
          }}
        >
          {/* Левая часть: пипетка + hex-инпут */}
          <Box sx={{ display: "flex", flexDirection: "column", gap: 1, width: "220px" }}>
            <SketchPicker
              color={color}
              onChange={(c) => setColor(c.hex)}
              presetColors={[]}
              styles={{
                default: {
                  picker: {
                    background: "transparent",
                    boxShadow: "none",
                    width: "100%",
                    height: "auto"
                  },
                  saturation: {
                    borderRadius: "8px"
                  },
                  hue: {
                    borderRadius: "8px"
                  }
                }
              }}
            />

            <input
              value={color}
              onChange={(e) => setColor(e.target.value)}
              style={{
                width: "100%",
                background: "transparent",
                color: darkMode ? "#00f6ff" : "#fff",
                border: darkMode ? "1px solid #00f6ff" : "1px solid rgb(203, 35, 144)",
                borderRadius: "8px",
                padding: "6px 12px",
                fontFamily: "monospace",
                textAlign: "center"
              }}
            />
          </Box>

          {/* Правая часть: пресетные цвета */}
          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: "repeat(8, 30px)",
              gap: 1.5,
              alignSelf: "center"
            }}
          >
            {presetColors.map((c) => (
              <Box
                key={c}
                onClick={() => setColor(c)}
                sx={{
                  width: 30,
                  height: 30,
                  borderRadius: "50%",
                  background: c,
                  border: "2px solidrgb(255, 255, 255)",
                  cursor: "pointer",
                  transition: "transform 0.2s",
                  "&:hover": {
                    transform: "scale(1.15)"
                  }
                }}
              />
            ))}
          </Box>
        </Box>
      </Box>

      <Button
        variant="contained"
        color="primary"
        className="save-btn"
        onClick={handleSave}
        disabled={!can}
        sx={{marginTop:"1rem"}}
      >
        {t("save_changes")}
      </Button>
    </Box>
  );
};

export default GeneralSettingsTab;
