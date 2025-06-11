import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Typography,
  Box,
  Chip,
  IconButton,
  MenuItem,
} from "@mui/material";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import { useProjects } from '../../../hooks/useProjects.js';
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import { SketchPicker } from "react-color"; // 🎨 Выбор цвета
import { useContext } from "react";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import "./AddProjectDialog.css";

const priorities = ["Low", "Medium", "High", "Critical"];
const statuses = ["Planned", "In Progress", "Completed", "On Hold"];
const icons = ["📁", "🚀", "📊", "⚙️", "📝", "🎯", "🛠️"]; // Иконки проектов
const presetColors = [
  "#FF0000", "#DC143C", "#B22222", "#8B0000", "#A52A2A", "#F08080", "#FA8072",
  "#FF6347", "#FF4500", "#FF8C00", "#FFA500", "#FFD700", "#FFC0CB", "#FF69B4",
  "#FF1493", "#DB7093", "#D2691E", "#FFFF00", "#F0E68C", "#ADFF2F", "#E6E6FA",
  "#32CD32", "#00FF00", "#008000", "#2E8B57", "#8FBC8F", "#808000", "#87CEEB",
  "#B0E0E6", "#00FFFF", "#00CED1", "#20B2AA", "#00FA9A", "#7FFFD4", "#40E0D0",
  "#5F9EA0", "#4682B4", "#0000FF", "#6A5ACD", "#4B0082", "#9932CC", "#800080",
  "#BA55D3", "#DA70D6", "#D8BFD8", "#C0C0C0", "#F5DEB3", "#000000"
];

const AddProjectDialog = ({ open, project, handleClose = () => { }, onSave = () => { } }) => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();

  const { addMemberToProject, removeMemberFromProject, fetchProjectMembers } = useProjects();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [members, setMembers] = useState([]);
  const [newMember, setNewMember] = useState("");
  const [deadline, setDeadline] = useState("");
  const [priority, setPriority] = useState("Medium");
  const [status, setStatus] = useState("Planned");
  const [color, setColor] = useState("#8b29bd");
  const [icon, setIcon] = useState("📁");
  const [resourceLinks, setResourceLinks] = useState([]);
  const [newResource, setNewResource] = useState("");
  const initializeForm = () => {
    if (project) {
      setTitle(project.title || "");
      setDescription(project.description || "");
      setMembers(project.members ? project.members.map((m) => `${m.username}`) : []);
      setDeadline(project.deadline || "");
      setPriority(project.priority || "Medium");
      setStatus(project.status || "Planned");
      setColor(project.color || "#9932CC");
      setIcon(project.icon || "📁");
      setResourceLinks(project.resourceLinks || []);
    } else {
      setTitle("");
      setDescription("");
      setMembers([]);
      setDeadline("");
      setPriority("Medium");
      setStatus("Planned");
      setColor("#9932CC");
      setIcon("📁");
      setResourceLinks([]);
    }
  };

  useEffect(() => {
    if (open) {
      initializeForm(); // ⬅️ вызываем только при открытии
    }
  }, [open]); // важно!

  // Добавляем нового пользователя (нужно указать username и roleId!)
  const handleAddMember = () => {
    if (newMember.trim()) {
      // укажи нужную роль, например, роль участника (roleId нужно узнать заранее!)
      const defaultRoleId = 2; // пример
      addMemberToProject(project.id, newMember, defaultRoleId)
        .then(() => {
          fetchProjectMembers(project.id).then(setMembers);
          setNewMember('');
        })
        .catch(err => console.error('Ошибка добавления участника:', err));
    }
  };

  // Удаляем пользователя по его username
  const handleDeleteMember = (memberUsername) => {
    removeMemberFromProject(project.id, memberUsername)
      .then(() => {
        fetchProjectMembers(project.id).then(setMembers);
      })
      .catch(err => console.error('Ошибка удаления участника:', err));
  };
  const handleAddResource = () => {
    if (newResource.trim() && !resourceLinks.includes(newResource)) {
      setResourceLinks([...resourceLinks, newResource]);
      setNewResource("");
    }
  };

  const handleDeleteResource = (resourceToDelete) => {
    setResourceLinks(resourceLinks.filter((res) => res !== resourceToDelete));
  };

  const handleSave = () => {

    const projectData = {
      title,
      description,
      members,
      deadline,
      priority,
      status,
      color,
      icon,
      resourceLinks,
    };

    onSave(projectData);
    handleClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="md" className={`${darkMode ? "night" : "day"}`}
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
          opacity: darkMode ? "0.93" : "0.8"
        },
      }}>
      <DialogTitle
        sx={{

          color: darkMode ? "#00f6ff" : "#fff",
          textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px #ff90e8, 0 0 24px #ff90e8"

        }}
      >{project ? t("edit_project") : t("add_project")}</DialogTitle>
      <DialogContent sx={{

        color: darkMode ? "#00f6ff" : "#fff",

      }}>
        <Box className="project-meta">
          <TextField
            label={t("project_title")}
            fullWidth
            margin="normal"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
          <TextField
            select
            label={t("project_icon")}
            fullWidth
            margin="normal"
            value={icon}
            onChange={(e) => setIcon(e.target.value)}
          >
            {icons.map((i, idx) => (
              <MenuItem key={idx} value={i}>
                {i}
              </MenuItem>
            ))}
          </TextField>
        </Box>

        <TextField
          label={t("description")}
          fullWidth
          multiline
          rows={3}
          margin="normal"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <Box className="project-options">
          <TextField
            type="date"

            fullWidth
            margin="normal"
            value={deadline}
            onChange={(e) => setDeadline(e.target.value)}
          />
          <TextField
            select
            label={t("priority")}
            fullWidth
            margin="normal"
            value={priority}
            onChange={(e) => setPriority(e.target.value)}
          >
            {priorities.map((p, idx) => (
              <MenuItem key={idx} value={p}>
                {p}
              </MenuItem>
            ))}
          </TextField>

          <TextField
            select
            label={t("status")}
            fullWidth
            margin="normal"
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            {statuses.map((s, idx) => (
              <MenuItem key={idx} value={s}>
                {s}
              </MenuItem>
            ))}
          </TextField>
        </Box>

        <Box mt={2}>
          <Typography variant="subtitle1" fontSize={16}>
            {t("project_color")}
          </Typography>

          <Box
            sx={{
              display: "flex",
              gap: 3,
              padding: 1.5,
              background: darkMode ? "#2b2b60" : "#b28cd9",
              borderRadius: "12px",
              border: darkMode
                ? "1px solid #00f6ff"
                : "1px solid rgb(203, 35, 144)",
              boxShadow: darkMode
                ? "0 0 8px #00f6ff"
                : "0 0 8px rgb(203, 44, 165)",
              color: darkMode ? "#00f6ff" : "#fff",
              maxWidth: 500,
              mx: "auto", // центрирование, если нужно
            }}
          >
            {/* Левая часть */}
            <Box
              sx={{
                display: "flex",
                flexDirection: "column",
                gap: 1,
                width: 180,
              }}
            >
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
                      height: "auto",
                      transform: "scale(0.9)", // Уменьшаем масштаб
                      transformOrigin: "top left",
                      opacity: "1"
                    },
                    saturation: {
                      borderRadius: "6px",
                    },
                    hue: {
                      borderRadius: "6px",
                    },
                  },
                }}
              />

              <input
                value={color}
                onChange={(e) => setColor(e.target.value)}
                style={{
                  width: "100%",
                  background: "transparent",
                  color: darkMode ? "#00f6ff" : "#fff",
                  border: darkMode
                    ? "1px solid #00f6ff"
                    : "1px solid rgb(203, 35, 144)",
                  borderRadius: "6px",
                  padding: "4px 8px",
                  fontSize: "0.8rem",
                  fontFamily: "monospace",
                  textAlign: "center",
                }}
              />
            </Box>

            {/* Правая часть */}
            <Box
              sx={{
                display: "grid",
                gridTemplateColumns: "repeat(6, 26px)",
                gap: 1,
                alignSelf: "center",
              }}
            >
              {presetColors.map((c) => (
                <Box
                  key={c}
                  onClick={() => setColor(c)}
                  sx={{
                    width: 24,
                    height: 24,
                    borderRadius: "50%",
                    background: c,
                    border: "2px solid white",
                    cursor: "pointer",
                    transition: "transform 0.2s",
                    "&:hover": {
                      transform: "scale(1.1)",
                    },
                  }}
                />
              ))}
            </Box>
          </Box>
        </Box>

        {project && (
          <Box mt={2}>
            <Typography variant="subtitle1">{t("members")}</Typography>
            <Box className="members-input">
              <TextField label={t("add_member")} value={newMember} onChange={(e) => setNewMember(e.target.value)} fullWidth />
              <Button variant="contained" color="primary" onClick={handleAddMember} startIcon={<PersonAddIcon />}>{t("add")}</Button>
            </Box>

            <Box className="members-list">
              {members.map((memberName, index) => (
                <Chip
                  key={index}
                  label={memberName}
                  onDelete={() => handleDeleteMember(memberName)}
                  deleteIcon={<HighlightOffIcon />}
                />
              ))}
            </Box>
          </Box>
        )}
        {project && (
          <Box mt={2}>
            <Typography variant="subtitle1">{t("resources")}</Typography>
            <Box className="resource-input">
              <TextField label={t("add_resource")} value={newResource} onChange={(e) => setNewResource(e.target.value)} fullWidth />
              <Button variant="contained" color="primary" onClick={handleAddResource}>{t("add")}</Button>
            </Box>
            <Box className="resource-list">
              {resourceLinks.map((res, idx) => (
                <Chip key={idx} label={res} onDelete={() => handleDeleteResource(res)} className="resource-chip" />
              ))}
            </Box>

          </Box>
        )}
      </DialogContent>
      <DialogActions
        sx={{
          backgroundColor: darkMode ? "rgba(43, 43, 96, 0.01)" : "rgba(255, 255, 255, 0.01)",


        }}
      >
        <Button onClick={handleClose}>{t("cancel")}</Button>
        <Button onClick={handleSave} color="primary" variant="contained">
          {t("save")}
        </Button>
      </DialogActions>
    </Dialog >
  );
};

export default AddProjectDialog;
