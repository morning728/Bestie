import React, { useState, useEffect, useContext } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  IconButton,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import AddIcon from "@mui/icons-material/Add";
import { SketchPicker } from "react-color";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import { getProjectTags, addProjectTag, deleteProjectTag, updateProjectTag } from "../../../services/api";
import "../ProjectSettingsPage.css";

const TagsTab = ({ projectId }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);

  const [tags, setTags] = useState([]);
  const [newTagName, setNewTagName] = useState("");
  const [newTagColor, setNewTagColor] = useState("#9932CC");
  const [editingTag, setEditingTag] = useState(null);
  const [editTagName, setEditTagName] = useState("");
  const [editTagColor, setEditTagColor] = useState("#9932CC");

  // Функция загрузки тегов
  const fetchTags = () => {
    getProjectTags(projectId)
      .then((response) => {
        if (response.data) {
          setTags(response.data);
        } else {
          setTags([]);
        }
      })
      .catch((err) => console.error("Ошибка получения тегов:", err));
  };

  useEffect(() => {
    fetchTags();
  }, [projectId]);

  // Функция добавления нового тега
  const handleAddTag = () => {
    if (newTagName.trim()) {
      addProjectTag(projectId, { name: newTagName, color: newTagColor })
        .then(() => {
          setNewTagName("");
          setNewTagColor("#9932CC");
          fetchTags();
        })
        .catch((err) => console.error("Ошибка добавления тега:", err));
    }
  };

  // Функция удаления тега
  const handleDeleteTag = (tagId) => {
    deleteProjectTag(projectId, tagId)
      .then(fetchTags)
      .catch((err) => console.error("Ошибка удаления тега:", err));
  };

  // Функция начала редактирования тега
  const startEditingTag = (tag) => {
    setEditingTag(tag.id);
    setEditTagName(tag.name);
    setEditTagColor(tag.color);
  };

  // Функция сохранения изменений тега
  const handleSaveTag = (tagId) => {
    updateProjectTag(projectId, { id: tagId, name: editTagName, color: editTagColor, projectId: projectId })
      .then(() => {
        setEditingTag(null);
        fetchTags();
      })
      .catch((err) => console.error("Ошибка обновления тега:", err));
  };

  return (
    <Box className={darkMode ? "settings-tab-content dark" : "settings-tab"}>
      <Typography variant="h6" gutterBottom>{t("tags")}</Typography>

      {/* Блок добавления нового тега */}
      <Box className="tag-input">
        <TextField
          fullWidth
          label={t("new_tag")}
          value={newTagName}
          onChange={(e) => setNewTagName(e.target.value)}
          sx={{ mr: 2 }}
        />
        <SketchPicker
          color={newTagColor}
          onChangeComplete={(color) => setNewTagColor(color.hex)}
          width="200px"
          styles={{
            default: {
              picker: { borderRadius: "10px", boxShadow: "0 4px 12px rgba(0,0,0,0.15)" },
            },
          }}
        />
        <Button
          variant="contained"
          sx={{ backgroundColor: newTagColor, color: "#fff", fontWeight: "bold" }}
          onClick={handleAddTag}
          startIcon={<AddIcon />}
        >
          {t("add")}
        </Button>
      </Box>

      {/* Список существующих тегов */}
      <Box mt={3}>
        <Typography variant="subtitle1">{t("existing_tags")}</Typography>
        <Box className="tags-list">
          {tags.length > 0 ? tags.map((tag) => (
            <Paper
              key={tag.id}
              className="tag-item"
              sx={{
                display: "flex",
                alignItems: "center",
                padding: 2,
                marginBottom: 1,
                background: `linear-gradient(135deg, ${tag.color} 0%, #000000 100%)`,
                borderRadius: "12px",
                boxShadow: "0 4px 10px rgba(0,0,0,0.3)",
                transition: "all 0.3s ease",
                "&:hover": {
                  transform: "scale(1.05)",
                  boxShadow: "0 6px 15px rgba(0,0,0,0.4)",
                },
              }}
            >
              {editingTag === tag.id ? (
                <>
                  <TextField
                    value={editTagName}
                    onChange={(e) => setEditTagName(e.target.value)}
                    sx={{ flexGrow: 1, backgroundColor: "#fff", borderRadius: 1, padding: "2px 8px" }}
                  />
                  <SketchPicker
                    color={editTagColor}
                    onChangeComplete={(color) => setEditTagColor(color.hex)}
                    width="160px"
                  />
                  <IconButton color="success" onClick={() => handleSaveTag(tag.id)}>
                    <SaveIcon />
                  </IconButton>
                </>
              ) : (
                <>
                  <Typography
                    sx={{
                      flexGrow: 1,
                      fontSize: "16px",
                      fontWeight: "bold",
                      color: "#fff",
                      padding: "6px 12px",
                    }}
                  >
                    {tag.name}
                  </Typography>
                  <IconButton color="primary" onClick={() => startEditingTag(tag)}>
                    <EditIcon />
                  </IconButton>
                  <IconButton color="error" onClick={() => handleDeleteTag(tag.id)}>
                    <DeleteIcon />
                  </IconButton>
                </>
              )}
            </Paper>
          )) : (
            <Typography variant="body2" color="textSecondary">{t("no_tags_available")}</Typography>
          )}
        </Box>
      </Box>
    </Box>
  );
};

export default TagsTab;
