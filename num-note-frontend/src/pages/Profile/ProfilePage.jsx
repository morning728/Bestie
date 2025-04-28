import React, { useEffect, useState, useContext } from "react";
import {
  Box,
  Typography,
  TextField,
  Button,
  Grid,
  Switch,
  CircularProgress,
} from "@mui/material";
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
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";
import "./ProfilePage.css";

const ProfilePage = () => {
  const { darkMode } = useContext(ThemeContext);
  const { t } = useTranslation();

  const [userInfo, setUserInfo] = useState(null);
  const [preferences, setPreferences] = useState(null);

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [emailInput, setEmailInput] = useState("");
  const [editEmailMode, setEditEmailMode] = useState(false);

  const [savingProfile, setSavingProfile] = useState(false);
  const [savingPreferences, setSavingPreferences] = useState(false);
  const [savingEmail, setSavingEmail] = useState(false);
  const [emailSent, setEmailSent] = useState(false); // новое состояние


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
        setEmailInput(preferencesResponse.data.email || "");
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
        taskReminder: updated.taskReminder,
      });
    } catch (error) {
      console.error("Ошибка при обновлении настроек уведомлений:", error);
    } finally {
      setSavingPreferences(false);
    }
  };

  const handleChangeEmail = async () => {
    if (!emailInput) return;
    setSavingEmail(true);
    try {
      await updateEmail(emailInput);
      setEmailSent(true); // Успешно отправили письмо
      const preferencesResponse = await getMyNotificationPreferences();
      setPreferences(preferencesResponse.data);
      setEditEmailMode(false); // Выключаем режим редактирования
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
          {/* Настройки уведомлений */}
          <Grid item xs={12} sx={{marginTop: 5}}>
            <Typography variant="h6" sx={{ mt: 4 }}>
              {t("notification_preferences")}
            </Typography>

            {[
              { field: "emailNotification", label: t("notify_by_email") },
              { field: "telegramNotification", label: t("notify_by_telegram") },
              { field: "inviteEnabled", label: t("receive_invites") },
              { field: "taskAssignedEnabled", label: t("task_assigned_notifications") },
              { field: "taskUpdatedEnabled", label: t("task_updated_notifications") },
              { field: "taskReminder", label: t("task_reminder") },
            ].map((item) => {
              const isMainChannel = item.field === "emailNotification" || item.field === "telegramNotification";
              const noChannelsActive = !preferences.emailNotification && !preferences.telegramNotification;
              const shouldDisable =
                savingPreferences ||
                (!isMainChannel && noChannelsActive); // Блокируем второстепенные переключатели, если нет каналов связи

              return (
                <Box key={item.field} display="flex" alignItems="center" justifyContent="space-between" mt={2}>
                  <Typography>{item.label}</Typography>
                  <Switch
                    checked={preferences[item.field]}
                    onChange={() => handleTogglePreference(item.field)}
                    disabled={shouldDisable}
                  />
                </Box>
              );
            })}


            {savingPreferences && (
              <Box display="flex" justifyContent="center" mt={2}>
                <CircularProgress size={24} />
              </Box>
            )}
          </Grid>
        </Grid>


        {/* Email-блок */}
        <Grid item xs={12} md={6}>
          <TextField
            label={t("email")}
            value={preferences.telegramId ? preferences.telegramId : "Login Via @BestieTrackerBot"}
            onChange={(e) => setEmailInput(e.target.value)}
            fullWidth
            margin="normal"
            disabled
          />
          <Box display="flex" alignItems="center" gap={1}>
            <TextField
              label={t("email")}
              value={preferences.emailVerified ? preferences.email : emailInput}
              onChange={(e) => setEmailInput(e.target.value)}
              fullWidth
              margin="normal"
              disabled={preferences.emailVerified && !editEmailMode}
            />
            {preferences.emailVerified ? (
              <CheckCircleIcon color="success" />
            ) : (
              <CancelIcon color="error" />
            )}
          </Box>

          {editEmailMode && preferences.emailVerified && (
            <TextField
              label={t("new_email")}
              placeholder="new email"
              value={emailInput}
              onChange={(e) => setEmailInput(e.target.value)}
              fullWidth
              margin="normal"
              sx={{ mt: 2 }}
            />
          )}

          <Button
            variant="contained"
            color="secondary"
            fullWidth
            onClick={() => {
              if (!preferences.emailVerified || editEmailMode) {
                handleChangeEmail();
              } else {
                setEditEmailMode(true);
              }
            }}
            disabled={savingEmail}
            sx={{ mt: 2 }}
          >
            {savingEmail ? (
              <CircularProgress size={24} />
            ) : editEmailMode || !preferences.emailVerified ? (
              t("verify_email")
            ) : (
              t("change_email")
            )}
          </Button>

          {/* Показываем сообщение после успешной отправки письма */}
          {emailSent && (
            <Typography mt={2} color="success.main">
              📧 Письмо для подтверждения отправлено. Проверьте почту и папку Спам!
            </Typography>
          )}
        </Grid>



      </Grid>
    </Box>
  );
};

export default ProfilePage;

