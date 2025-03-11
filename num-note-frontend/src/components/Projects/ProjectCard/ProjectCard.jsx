import React from "react";
import { Card, CardContent, Typography, Chip, Box, IconButton } from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import { useTranslation } from "react-i18next";
import "./ProjectCard.css";

const ProjectCard = ({ project, onClick, onEdit }) => {
  const { t } = useTranslation();

  return (
    <Card className="project-card" onClick={onClick} sx={{ borderLeft: `5px solid ${project.color}` }}>
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
