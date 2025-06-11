import {React, useContext} from "react";
import { Card, CardContent, Typography, Chip, Box, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import "./ProjectCard.css";

const ProjectCard = ({ project, onClick, onEdit }) => {
  const { t } = useTranslation();
  const { darkMode } = useContext(ThemeContext);

  return (
    <Card className="project-card" onClick={onClick} sx={{
        borderLeft: `5px solid ${project.color}`,
        backgroundColor: darkMode ? "rgba(255, 255, 255, 0.1)" : "rgba(255, 255, 255, 0.2)",
        boxShadow: darkMode
          ? "0 0 8px rgba(0, 246, 255, 0.3)"
          : "0 0 8px rgba(255, 105, 180, 0.3), 0 0 8px rgba(121, 179, 244, 0.5)",
        transition: "all 0.3s ease",
        paddingLeft: "10px",
        color: "inherit",
        "&:hover": {
          boxShadow: darkMode
            ? "0 0 12px rgba(0, 246, 255, 0.8)"
            : "0 0 12px 4px rgba(255, 105, 180, 0.3), 0 0 20px 8px rgba(121, 179, 244, 0.4)",
          transform: "scale(1.02)",
          color: "inherit"
        }
      }}>
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">{project.icon} {project.title}</Typography>
          <IconButton onClick={(e) => { e.stopPropagation(); onEdit(project); }}><EditIcon /></IconButton>
        </Box>
        <Typography variant="body2">{t("status")}: {project.status}</Typography>
        <Typography variant="body2">{t("priority")}: {project.priority}</Typography>
        <Typography variant="body2">{t("deadline")}: {project.deadline || t("no_deadline")}</Typography>

        {/* <Box mt={1}>
          <Typography variant="subtitle2">{t("members")}:</Typography>
          <Box display="flex" gap={1} flexWrap="wrap">
            {project.members.map((member, index) => (
              <Chip key={index} label={member} size="small" />
            ))}
          </Box>
        </Box> */}
      </CardContent>
    </Card>
  );
};

export default ProjectCard;
