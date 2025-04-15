import React, { useState, useEffect, useContext } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Paper,
  IconButton,
  Popover,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import AddIcon from "@mui/icons-material/Add";
import { SketchPicker } from "react-color";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import {
  getProjectTags,
  addProjectTag,
  deleteProjectTag,
  updateProjectTag,
} from "../../../services/api";
import { useProjectAccess } from "../../../context/ProjectAccessContext.js";
import "../ProjectSettingsPage.css";

const TagsTab = ({ projectId }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);

    const { me, hasPermission, loading } = useProjectAccess();
    const can = hasPermission("CAN_EDIT_PROJECT");

  const [tags, setTags] = useState([]);
  const [newTagName, setNewTagName] = useState("");
  const [newTagColor, setNewTagColor] = useState("#9932CC");
  const [editingTag, setEditingTag] = useState(null);
  const [editTagName, setEditTagName] = useState("");
  const [editTagColor, setEditTagColor] = useState("#9932CC");

  // Для popover выбора цвета
  const [colorAnchor, setColorAnchor] = useState(null);
  const [editColorAnchor, setEditColorAnchor] = useState(null);

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
    updateProjectTag(projectId, {
      id: tagId,
      name: editTagName,
      color: editTagColor,
      projectId: projectId,
    })
      .then(() => {
        setEditingTag(null);
        fetchTags();
      })
      .catch((err) => console.error("Ошибка обновления тега:", err));
  };

  return (
    <Box className={darkMode ? "settings-tab-content dark" : "settings-tab"} sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        {t("tags")}
      </Typography>

      {/* Блок добавления нового тега */}
      <Paper sx={{ p: 3, mb: 3, display: "flex", alignItems: "center", gap: 2 }}>
        <TextField
          fullWidth
          label={t("new_tag")}
          value={newTagName}
          onChange={(e) => setNewTagName(e.target.value)}
          disabled={!can}
        />
        <Box
          sx={{
            width: 40,
            height: 40,
            borderRadius: "8px",
            background: `linear-gradient(135deg, ${newTagColor},rgb(130, 127, 127))`,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            border: "1px solid #ccc",
            cursor: "pointer",
            transition: "0.3s",
            "&:hover": { opacity: 0.8 },
          }}
          onClick={(e) => setColorAnchor(e.currentTarget)}
          disabled={!can}
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
          disabled={!can}
        >
          <SketchPicker
            color={newTagColor}
            onChangeComplete={(color) => {
              setNewTagColor(color.hex);
              setColorAnchor(null);
            }}
            disabled={!can}
          />
        </Popover>
        <Button
          variant="contained"
          color="primary"
          onClick={handleAddTag}
          startIcon={<AddIcon />}
          sx={{ whiteSpace: "nowrap" }}
          disabled={!can}
        >
          {t("add")}
        </Button>
      </Paper>

      {/* Список существующих тегов */}
      <Box>
        <Typography variant="subtitle1" sx={{ mb: 2 }}>
          {t("existing_tags")}
        </Typography>
        {tags.length > 0 ? (
          tags.map((tag) => (
            <Paper
              key={tag.id}
              sx={{
                p: 2,
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                background: `linear-gradient(135deg, ${tag.color},rgb(44, 43, 43))`,
                color: "#fff",
                borderRadius: "12px",
                transition: "0.3s",
                "&:hover": { opacity: 0.9 },
                mb: 1,
              }}
            >
              {editingTag === tag.id ? (
                <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                  <TextField
                    value={editTagName}
                    onChange={(e) => setEditTagName(e.target.value)}
                    size="small"
                    sx={{ backgroundColor: "#fff", borderRadius: "4px" }}
                  />
                  <Box
                    sx={{
                      width: 40,
                      height: 40,
                      borderRadius: "8px",
                      background: `linear-gradient(135deg, ${editTagColor},rgb(21, 20, 20))`,
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
                      color={editTagColor}
                      onChangeComplete={(color) => {
                        setEditTagColor(color.hex);
                        setEditColorAnchor(null);
                      }}
                    />
                  </Popover>
                  <IconButton onClick={() => handleSaveTag(tag.id)} color="success">
                    <SaveIcon />
                  </IconButton>
                </Box>
              ) : (
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <Typography variant="subtitle1">{tag.name}</Typography>
                  <IconButton onClick={() => startEditingTag(tag)} color="default" disabled={!can}>
                    <EditIcon />
                  </IconButton>
                </Box>
              )}
              <IconButton onClick={() => handleDeleteTag(tag.id)} color="error" disabled={!can}>
                <DeleteIcon />
              </IconButton>
            </Paper>
          ))
        ) : (
          <Typography variant="body2" color="textSecondary">
            {t("no_tags_available")}
          </Typography>
        )}
      </Box>
    </Box>
  );
};

export default TagsTab;
