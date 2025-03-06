import React, { useState } from "react";
import { Dialog, DialogActions, DialogContent, DialogTitle, Button, TextField, Grid, IconButton, Box, Typography } from "@mui/material";
import ClearIcon from "@mui/icons-material/Clear"; // Иконка для удаления тега

const AddTagDialog = ({ open, onClose, handleAddTag, existingTags }) => {
  const [newTagName, setNewTagName] = useState("");
  const [newTagColor, setNewTagColor] = useState("#000000");

  const handleTagNameChange = (e) => setNewTagName(e.target.value);
  const handleTagColorChange = (e) => setNewTagColor(e.target.value);

  const handleSaveTag = () => {
    if (newTagName.trim()) {
      handleAddTag({ name: newTagName, color: newTagColor });
      setNewTagName("");
      setNewTagColor("#000000"); // Сброс после добавления
    }
  };

  const handleDeleteTag = (tagToDelete) => {
    // Удаление тега
    const updatedTags = existingTags.filter((tag) => tag.name !== tagToDelete.name);
    handleAddTag(updatedTags);
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Add New Tag</DialogTitle>
      <DialogContent>
        <Box display="flex" gap={2}>
          {/* Существующие теги */}
          <Box sx={{ width: "40%", maxHeight: "300px", overflowY: "auto" }}>
            <Typography variant="h6" gutterBottom>Existing Tags</Typography>
            {existingTags.map((tag, index) => (
              <Box key={index} display="flex" alignItems="center" gap={1} mb={1}>
                <Box sx={{ width: "20px", height: "20px", backgroundColor: tag.color }} />
                <Typography>{tag.name}</Typography>
                <IconButton onClick={() => handleDeleteTag(tag)} color="error">
                  <ClearIcon />
                </IconButton>
              </Box>
            ))}
          </Box>

          {/* Форма добавления нового тега */}
          <Box sx={{ width: "80%" }}>
            <TextField
              label="New Tag Name"
              fullWidth
              value={newTagName}
              onChange={handleTagNameChange}
              margin="normal"
            />
            <TextField
              label="Tag Color"
              fullWidth
              type="color"
              value={newTagColor}
              onChange={handleTagColorChange}
              margin="normal"
            />
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
        <Button onClick={handleSaveTag} color="primary" variant="contained">
          Save Tag
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddTagDialog;
