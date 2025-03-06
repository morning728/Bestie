import React, { useState } from "react";
import { Dialog, DialogActions, DialogContent, DialogTitle, Button, TextField, Grid, IconButton, Box, Typography } from "@mui/material";
import ClearIcon from "@mui/icons-material/Clear"; // Иконка для удаления тега

const AddStatusDialog = ({ open, onClose, handleAddStatus, existingStatuses }) => {
  const [newStatusName, setNewStatusName] = useState("");
  const [newStatusColor, setNewStatusColor] = useState("#000000");

  const handleStatusNameChange = (e) => setNewStatusName(e.target.value);
  const handleStatusColorChange = (e) => setNewStatusColor(e.target.value);

  const handleSaveStatus = () => {
    if (newStatusName.trim()) {
      handleAddStatus({ name: newStatusName, color: newStatusColor });
      setNewStatusName("");
      setNewStatusColor("#000000"); // Сброс после добавления
    }
  };

  const handleDeleteStatus = (statusToDelete) => {
    // Удаление тега
    const updatedStatuses = existingStatuses.filter((status) => status.name !== statusToDelete.name);
    handleAddStatus(updatedStatuses);
  };

  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Add New Status</DialogTitle>
      <DialogContent>
        <Box display="flex" gap={2}>
          {/* Существующие теги */}
          <Box sx={{ width: "40%", maxHeight: "300px", overflowY: "auto" }}>
            <Typography variant="h6" gutterBottom>Existing Statuses</Typography>
            {existingStatuses.map((status, index) => (
              <Box key={index} display="flex" alignItems="center" gap={1} mb={1}>
                <Box sx={{ width: "20px", height: "20px", backgroundColor: status.color }} />
                <Typography>{status.name}</Typography>
                <IconButton onClick={() => handleDeleteStatus(status)} color="error">
                  <ClearIcon />
                </IconButton>
              </Box>
            ))}
          </Box>

          {/* Форма добавления нового тега */}
          <Box sx={{ width: "80%" }}>
            <TextField
              label="New Status Name"
              fullWidth
              value={newStatusName}
              onChange={handleStatusNameChange}
              margin="normal"
            />
            <TextField
              label="Status Color"
              fullWidth
              type="color"
              value={newStatusColor}
              onChange={handleStatusColorChange}
              margin="normal"
            />
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
        <Button onClick={handleSaveStatus} color="primary" variant="contained">
          Save Status
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddStatusDialog;
