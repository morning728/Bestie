import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
  Chip,
  IconButton
} from "@mui/material";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import { useTranslation } from "react-i18next";
import { useContext, useState } from "react";
import { ThemeContext } from "../../../ThemeContext";
import "./ProjectDetailsDialog.css";

const ProjectDetailsDialog = ({ open, project, handleClose, onEdit, onDelete, onRemoveMember }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);
  const [openConfirm, setOpenConfirm] = useState(false);
  if (!project) return null;

  return (
    <Dialog PaperProps={{
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
    }} open={open} onClose={handleClose} fullWidth maxWidth="sm" className={` ${darkMode ? "night" : "day"}`}>
      <DialogTitle
        sx={{
          
          color: darkMode ? "#00f6ff" : "#fff",
          textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12pxrgb(220, 75, 191), 0 0 24px #ff90e8",

        }}>{project.icon} {project.title}</DialogTitle>
      <DialogContent className={`project-details-dialog ${darkMode ? "night" : "day"}`}
        sx={{
          
          color: darkMode ? "#00f6ff" : "#fff",
          textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12pxrgb(220, 75, 191), 0 0 24px #ff90e8",

        }}>
        <Typography variant="subtitle1" gutterBottom>{t("description")}:</Typography>
        <Typography variant="body2" paragraph>{project.description}</Typography>

        <Box mt={2}>
          <Typography variant="subtitle1">{t("status")}: {project.status}</Typography>
          <Typography variant="subtitle1">{t("priority")}: {project.priority}</Typography>
          <Typography variant="subtitle1">{t("deadline")}: {project.deadline || t("no_deadline")}</Typography>
        </Box>

        <Box mt={2}>
          <Typography variant="subtitle1">{t("members")}:</Typography>
          <Box className="members-box" sx={{
            color: darkMode ? "#00f6ff" : "#fff",
            textShadow: "none",

          }}>
            {project.members && project.members.length > 0 ? (
              project.members.map((member) => (
                <Chip
                  sx={{
                    color: darkMode ? "#00f6ff" : "#fff",
                    textShadow: "none",

                  }}
                  key={member.userId}
                  label={`${member.firstName} ${member.lastName}`}
                  deleteIcon={<HighlightOffIcon />}
                  // onDelete={() => removeMember(project.id, member.username)}
                  className="member-chip"
                />
              ))
            ) : (
              <Typography variant="caption">{t("no_members")}</Typography>
            )}
          </Box>
        </Box>
      </DialogContent>
      <DialogActions
        sx={{
          
          color: darkMode ? "#00f6ff" : "#fff",
          textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12pxrgb(220, 75, 191), 0 0 24px #ff90e8",

        }}>
        <Button onClick={handleClose}>{t("close")}</Button>
        <Button onClick={() => onEdit(project.id)} color="primary">{t("edit")}</Button>
        <>
          <Button onClick={() => setOpenConfirm(true)} color="error">
            {t("delete")}
          </Button>

          <Dialog open={openConfirm} onClose={() => setOpenConfirm(false)}>
            <DialogTitle>{t("confirm_delete_title")}</DialogTitle>
            <DialogContent>{t("confirm_delete_project")}</DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenConfirm(false)}>{t("cancel")}</Button>
              <Button onClick={() => onDelete(project.id)} color="error">{t("delete")}</Button>
            </DialogActions>
          </Dialog>
        </>
      </DialogActions>
    </Dialog>
  );
};

export default ProjectDetailsDialog;
