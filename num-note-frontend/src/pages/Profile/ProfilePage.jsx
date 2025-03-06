import React, { useContext, useState } from "react";
import { useTranslation } from "react-i18next";
import { Box, Typography, Button, TextField, Grid } from "@mui/material";
import { ThemeContext } from "../../ThemeContext";
import ChangePasswordDialog from "../../components/ChangePasswordDialog/ChangePasswordDialog";
import AddTagDialog from "../../components/AddTagAndStatusDialog/AddTagDialog"; // Импортируем диалог для тегов
import AddStatusDialog from "../../components/AddTagAndStatusDialog/AddStatusDialog"; // Импортируем диалог для статусов
import "./ProfilePage.css";
import Header from "../../components/Header/Header";

const ProfilePage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t, i18n } = useTranslation(); // Получаем t для локализации
  const [openPasswordDialog, setOpenPasswordDialog] = useState(false);
  const [openTagDialog, setOpenTagDialog] = useState(false);
  const [openStatusDialog, setOpenStatusDialog] = useState(false);
  const [tags, setTags] = useState([
    { name: "Work", color: "#FF0000" },
    { name: "Health", color: "#00FF00" },
  ]);
  const [statuses, setStatuses] = useState([
    { name: "Pending" },
    { name: "Completed" },
    { name: "In Progress" },
  ]);

  const handlePasswordDialogOpen = () => setOpenPasswordDialog(true);
  const handlePasswordDialogClose = () => setOpenPasswordDialog(false);

  const handleTagDialogOpen = () => setOpenTagDialog(true);
  const handleTagDialogClose = () => setOpenTagDialog(false);

  const handleStatusDialogOpen = () => setOpenStatusDialog(true);
  const handleStatusDialogClose = () => setOpenStatusDialog(false);

  const handleAddTag = (newTag) => {
    if (Array.isArray(newTag)) {
      setTags(newTag);
    } else {
      setTags([...tags, newTag]);
    }
  };

  const handleAddStatus = (newStatus) => {
    if (Array.isArray(newStatus)) {
      setStatuses(newStatus);
    } else {
      setStatuses([...statuses, newStatus]);
    }
  };

  return (
    <Box className={`profile-page ${darkMode ? "dark" : "light"}`}>
      <Header />
      <Typography variant="h4" gutterBottom className="profile-title">
        {t("profile")} {/* Используем перевод для "Profile" */}
      </Typography>

      <Grid container spacing={3} className="profile-form">
        {/* Изменяемые поля (Username, Email) */}
        <Grid item xs={12} md={6}>
          <TextField label={t("profile_username")} fullWidth defaultValue="JohnDoe" margin="normal" />
        </Grid>

        {/* Email */}
        <Grid item xs={12} md={6}>
          <TextField label={t("profile_email")} fullWidth defaultValue="johndoe@example.com" margin="normal" />
        </Grid>

        {/* Кнопка "Change Password" */}
        <Grid item xs={12} md={6}>
          <Button variant="contained" color="primary" fullWidth size="small" onClick={handlePasswordDialogOpen}>
            {t("profile_change_password")} {/* Используем перевод для "Change Password" */}
          </Button>
        </Grid>

        {/* Теги и Статусы */}
        <Grid item xs={12} md={6}>
          <Button className="profile-btn" variant="contained" color="secondary" fullWidth size="small" onClick={handleTagDialogOpen}>
            {t("profile_add_new_tag")} {/* Используем перевод для "Add New Tag" */}
          </Button>
          <Button className="profile-btn" variant="contained" color="secondary" fullWidth size="small" onClick={handleStatusDialogOpen}>
            {t("profile_add_new_status")} {/* Используем перевод для "Add New Status" */}
          </Button>
        </Grid>

        {/* Кнопка "Save Changes" под всеми полями */}
        <Grid item xs={12}>
          <Button variant="contained" color="secondary" fullWidth size="large">
            {t("profile_save_changes")} {/* Используем перевод для "Save Changes" */}
          </Button>
        </Grid>
      </Grid>

      {/* Диалоги */}
      <ChangePasswordDialog open={openPasswordDialog} onClose={handlePasswordDialogClose} />
      <AddTagDialog open={openTagDialog} onClose={handleTagDialogClose} handleAddTag={handleAddTag} existingTags={tags} />
      <AddStatusDialog open={openStatusDialog} onClose={handleStatusDialogClose} handleAddStatus={handleAddStatus} existingStatuses={statuses} />
    </Box>
  );
};

export default ProfilePage;
