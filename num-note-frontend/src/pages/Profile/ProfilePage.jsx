import React, { useEffect, useState, useContext } from "react";
import { Box, Typography, TextField, Button, Grid, Switch, CircularProgress } from "@mui/material";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../ThemeContext";
import {
  getMeExpanded,
  getMyNotificationPreferences,
  updateUserProfile,
  updateNotificationPreferences,
  updateEmail,
} from "../../services/api";
import Header from "../../components/Header/Header";
import "./ProfilePage.css";

const ProfilePage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();

  const [userInfo, setUserInfo] = useState(null);
  const [preferences, setPreferences] = useState(null);

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [newEmail, setNewEmail] = useState("");

  const [savingProfile, setSavingProfile] = useState(false);
  const [savingPreferences, setSavingPreferences] = useState(false);
  const [savingEmail, setSavingEmail] = useState(false);

  useEffect(() => {
    async function fetchData() {
      try {
        const [userInfoResponse, preferencesResponse] = await Promise.all([
          getMeExpanded(),
          getMyNotificationPreferences(),
        ]);
        setUserInfo(userInfoResponse.data);
        setPreferences(preferencesResponse.data);

        setFirstName(userInfoResponse.data.firstName || "");
        setLastName(userInfoResponse.data.lastName || "");
        setNewEmail(preferencesResponse.data.email || "");
      } catch (error) {
        console.error("Ошибка загрузки данных профиля:", error);
      }
    }
    fetchData();
  }, []);

  const handleSaveProfile = async () => {
    if (!userInfo) return;
    setSavingProfile(true);
    try {
      await updateUserProfile(userInfo.username, {
        ...userInfo,
        firstName,
        lastName,
      });
    } catch (error) {
      console.error("Ошибка при обновлении профиля:", error);
    } finally {
      setSavingProfile(false);
    }
  };

  const handleTogglePreference = async (field) => {
    if (!preferences) return;
    const updated = { ...preferences, [field]: !preferences[field] };
    setPreferences(updated);
    setSavingPreferences(true);
    try {
      await updateNotificationPreferences({
        emailNotification: updated.emailNotification,
        telegramNotification: updated.telegramNotification,
        inviteEnabled: updated.inviteEnabled,
        taskAssignedEnabled: updated.taskAssignedEnabled,
        taskUpdatedEnabled: updated.taskUpdatedEnabled,
        taskDeadlineReminder: updated.taskDeadlineReminder,
      });
    } catch (error) {
      console.error("Ошибка при обновлении настроек уведомлений:", error);
    } finally {
      setSavingPreferences(false);
    }
  };

  const handleChangeEmail = async () => {
    if (!newEmail) return;
    setSavingEmail(true);
    try {
      await updateEmail(newEmail);
      const preferencesResponse = await getMyNotificationPreferences();
      setPreferences(preferencesResponse.data);
    } catch (error) {
      console.error("Ошибка при изменении email:", error);
    } finally {
      setSavingEmail(false);
    }
  };

  if (!userInfo || !preferences) {
    return (
      <Box className={`profile-page ${darkMode ? "dark" : "light"}`}>
        <Typography variant="h6" align="center" mt={5}>
          {t("loading")}...
        </Typography>
      </Box>
    );
  }

  return (
    <Box className={`profile-page ${darkMode ? "dark" : "light"}`}>
      <Header />
      <Typography variant="h4" gutterBottom className="profile-title">
        {t("profile")}
      </Typography>

      <Grid container spacing={3} mt={2}>
        {/* Имя и фамилия */}
        <Grid item xs={12} md={6}>
          <TextField
            label={t("first_name")}
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            fullWidth
            margin="normal"
          />
          <TextField
            label={t("last_name")}
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            fullWidth
            margin="normal"
          />
          <Button
            variant="contained"
            color="primary"
            fullWidth
            onClick={handleSaveProfile}
            disabled={savingProfile}
            sx={{ mt: 2 }}
          >
            {savingProfile ? <CircularProgress size={24} /> : t("save_profile")}
          </Button>
        </Grid>

        {/* Email (редактируемое поле) */}
        <Grid item xs={12} md={6}>
          <TextField
            label={t("email")}
            value={newEmail}
            onChange={(e) => setNewEmail(e.target.value)}
            fullWidth
            margin="normal"
          />
          <Button
            variant="contained"
            color="secondary"
            fullWidth
            onClick={handleChangeEmail}
            disabled={savingEmail}
            sx={{ mt: 2 }}
          >
            {savingEmail ? <CircularProgress size={24} /> : t("change_email")}
          </Button>
        </Grid>

        {/* Настройки уведомлений */}
        <Grid item xs={12}>
          <Typography variant="h6" sx={{ mt: 4 }}>
            {t("notification_preferences")}
          </Typography>

          {[
            { field: "emailNotification", label: t("notify_by_email") },
            { field: "telegramNotification", label: t("notify_by_telegram") },
            { field: "inviteEnabled", label: t("receive_invites") },
            { field: "taskAssignedEnabled", label: t("task_assigned_notifications") },
            { field: "taskUpdatedEnabled", label: t("task_updated_notifications") },
            { field: "taskDeadlineReminder", label: t("task_deadline_reminder") },
          ].map((item) => (
            <Box key={item.field} display="flex" alignItems="center" justifyContent="space-between" mt={2}>
              <Typography>{item.label}</Typography>
              <Switch
                checked={preferences[item.field]}
                onChange={() => handleTogglePreference(item.field)}
                disabled={savingPreferences}
              />
            </Box>
          ))}

          {savingPreferences && (
            <Box display="flex" justifyContent="center" mt={2}>
              <CircularProgress size={24} />
            </Box>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

export default ProfilePage;
