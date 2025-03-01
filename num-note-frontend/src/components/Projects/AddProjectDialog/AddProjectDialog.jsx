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
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import { SketchPicker } from "react-color"; // 🎨 Выбор цвета
import { useContext } from "react";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import "./AddProjectDialog.css";

const priorities = ["Low", "Medium", "High", "Critical"];
const statuses = ["Planned", "In Progress", "Completed", "On Hold"];
const icons = ["📁", "🚀", "📊", "⚙️", "📝", "🎯", "🛠️"]; // Иконки проектов

const AddProjectDialog = ({ open, project, handleClose = () => { }, onSave = () => { } }) => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [members, setMembers] = useState([]);
  const [newMember, setNewMember] = useState("");
  const [deadline, setDeadline] = useState("");
  const [priority, setPriority] = useState("Medium");
  const [status, setStatus] = useState("Planned");
  const [color, setColor] = useState("#9932CC");
  const [icon, setIcon] = useState("📁");
  const [resourceLinks, setResourceLinks] = useState([]);
  const [newResource, setNewResource] = useState("");

  useEffect(() => {
    if (project) {
      setTitle(project.title || "");
      setDescription(project.description || "");
      setMembers(project.members || []);
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
  }, [project]);

  const handleAddMember = () => {
    if (newMember.trim() && !members.includes(newMember)) {
      setMembers([...members, newMember]);
      setNewMember("");
    }
  };

  const handleDeleteMember = (memberToDelete) => {
    setMembers(members.filter((member) => member !== memberToDelete));
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
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="md">
      <DialogTitle
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}
      >{project ? t("edit_project") : t("add_project")}</DialogTitle>
      <DialogContent className={darkMode ? "add-project-dialog dark" : "add-project-dialog"}>
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
            label={t("deadline")}
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
          <Typography variant="subtitle1">{t("project_color")}</Typography>
          <SketchPicker color={color} onChange={(c) => setColor(c.hex)} />
        </Box>

        <Box mt={2}>
          <Typography variant="subtitle1">{t("members")}</Typography>
          <Box className="members-input">
            <TextField label={t("add_member")} value={newMember} onChange={(e) => setNewMember(e.target.value)} fullWidth  />
            <Button variant="contained" color="primary" onClick={handleAddMember} startIcon={<PersonAddIcon />}>{t("add")}</Button>
          </Box>

          <Box className="members-list">
            {members.map((member, index) => (
              <Chip key={index} label={member} onDelete={() => handleDeleteMember(member)} deleteIcon={<HighlightOffIcon />} />
            ))}
          </Box>
        </Box>

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
      </DialogContent>
      <DialogActions
        sx={{
          backgroundColor: darkMode ? "#2b2b60" : "white",
          color: darkMode ? "white" : "black",
        }}>
        <Button onClick={handleClose}>{t("cancel")}</Button>
        <Button onClick={handleSave} color="primary" variant="contained">
          {t("save")}
        </Button>
      </DialogActions>
    </Dialog >
  );
};

export default AddProjectDialog;
