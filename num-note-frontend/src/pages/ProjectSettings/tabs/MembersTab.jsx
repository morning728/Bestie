import React, { useState, useEffect } from "react";
import {
  Box,
  Button,
  TextField,
  MenuItem,
  Typography,
  Chip,
  IconButton,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import "./MembersTab.css";
import {
    getProjectMembers ,
  addUserToProject,
  removeUserFromProject,
  changeUserRole,
  getRolesByProjectId,
} from "../../../services/api";

const MembersTab = ({ projectId }) => {
  const { t } = useTranslation();

  const [members, setMembers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [newMember, setNewMember] = useState("");
  const [selectedRole, setSelectedRole] = useState("");

  useEffect(() => {
    loadMembersAndRoles();
  }, [projectId]);

  const loadMembersAndRoles = () => {
    getProjectMembers(projectId).then((res) => setMembers(res.data));
    getRolesByProjectId(projectId).then((res) => {
      setRoles(res.data);
      if (res.data.length > 0) setSelectedRole(res.data[0].id);
    });
  };

  const handleAddMember = () => {
    if (!newMember.trim()) return;

    addUserToProject(projectId, {
      username: newMember,
      roleId: selectedRole,
    })
      .then(loadMembersAndRoles)
      .catch((err) => console.error("Ошибка добавления участника:", err));

    setNewMember("");
  };

  const handleRemoveMember = (username) => {
    removeUserFromProject(projectId, username)
      .then(loadMembersAndRoles)
      .catch((err) => console.error("Ошибка удаления участника:", err));
  };

  const handleChangeUserRole = (username, roleId) => {
    changeUserRole(projectId, { username, roleId })
      .then(loadMembersAndRoles)
      .catch((err) => console.error("Ошибка смены роли:", err));
  };

  return (
    <Box className="members-tab">
      <Typography variant="h6" gutterBottom>
        {t("project_members")}
      </Typography>

      <Box className="add-member-section">
        <TextField
          label={t("username")}
          value={newMember}
          onChange={(e) => setNewMember(e.target.value)}
        />
        <TextField
          select
          label={t("role")}
          value={selectedRole}
          onChange={(e) => setSelectedRole(e.target.value)}
        >
          {roles.map((role) => (
            <MenuItem key={role.id} value={role.id}>
              {role.name}
            </MenuItem>
          ))}
        </TextField>
        <Button
          variant="contained"
          color="primary"
          startIcon={<PersonAddIcon />}
          onClick={handleAddMember}
        >
          {t("add_member")}
        </Button>
      </Box>

      <Box className="members-list">
        {members.map((member) => (
          <Box key={member.userId} className="member-item">
            <Typography>{member.username}</Typography>
            <TextField
              select
              size="small"
              value={member.roleId}
              onChange={(e) =>
                handleChangeUserRole(member.username, e.target.value)
              }
            >
              {roles.map((role) => (
                <MenuItem key={role.id} value={role.id}>
                  {role.name}
                </MenuItem>
              ))}
            </TextField>
            <IconButton
              color="error"
              onClick={() => handleRemoveMember(member.username)}
            >
              <HighlightOffIcon />
            </IconButton>
          </Box>
        ))}
      </Box>
    </Box>
  );
};

export default MembersTab;
