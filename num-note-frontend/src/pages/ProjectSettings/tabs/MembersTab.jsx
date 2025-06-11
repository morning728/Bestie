// components/MemberTab.jsx
import React, { useState, useEffect, useContext } from "react";
import { ThemeContext } from "../../../ThemeContext";
import {
  Box,
  Typography,
  TextField,
  MenuItem,
  Button,
  Autocomplete,
  IconButton,
  Snackbar,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Select,
} from "@mui/material";
import { useTranslation } from "react-i18next";
import DeleteIcon from "@mui/icons-material/Delete";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import { useMembers } from "../../../hooks/useMembers.js";
import { useProjectAccess } from "../../../context/ProjectAccessContext.js";

const MembersTab = ({ projectId }) => {
  const { t } = useTranslation();
  const {
    members,
    roles,
    searchResults,
    universalInvite,
    copyStatus,
    searchUsersHandler,
    addUser,
    removeUser,
    changeRole,
    createUniversalLink,
    copyToClipboard,
  } = useMembers(projectId);
  const { darkMode } = useContext(ThemeContext);


  const { me, hasPermission, loading } = useProjectAccess();
  const canManageMembers = hasPermission("CAN_MANAGE_MEMBERS");
  const canManageRoles = hasPermission("CAN_MANAGE_ROLES");

  const [selectedUser, setSelectedUser] = useState(null);
  const [selectedRole, setSelectedRole] = useState(null);
  const [universalRole, setUniversalRole] = useState(null)


  const handleGenerateInvite = async () => {
    if (selectedUser && selectedRole) {
      const link = await addUser(selectedUser.username, selectedRole.id, false);
      if (link) copyToClipboard(link);
    }
  };

  const handleDirectInvite = async () => {
    if (selectedUser && selectedRole) {
      await addUser(selectedUser.username, selectedRole.id, true);
    }
  };

  const handleUniversalLink = async () => {
    if (universalRole) {
      await createUniversalLink(universalRole.id);
    }
  };


  if (loading) return <Typography>Загрузка данных доступа...</Typography>;

  return (
    <Box sx={{ p: 3,
          color: darkMode ? "#00f6ff" : "#fff",

        }}>
      <Typography variant="h6" gutterBottom sx={{textShadow: darkMode ? "0 0 6px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px rgb(199, 50, 182), 0 0 24pxrgb(199, 48, 136)",}}>
        {t("members_invite_user")}
      </Typography>

      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <Autocomplete
          options={searchResults}
          getOptionLabel={(option) => option.username}
          onInputChange={(_, value) => searchUsersHandler(value)}
          onChange={(_, value) => setSelectedUser(value)}
          renderInput={(params) => <TextField {...params} label={t("members_user")} />}
          sx={{ width: 300 }}
          disabled={!canManageMembers}
        />
        <TextField
          select
          label={t("members_role")}

          value={selectedRole?.id || ""}
          onChange={(e) =>
            setSelectedRole(roles.find((r) => r.id === +e.target.value))
          }
          sx={{ width: 200 }}
          disabled={!canManageMembers}
        >
          {roles.map((role) => (
            <MenuItem key={role.id} value={role.id}>
              {role.name}
            </MenuItem>
          ))}
        </TextField>
        <Button disabled={!canManageMembers} variant="outlined" onClick={handleGenerateInvite}>
          {t("members_invite_link")}
        </Button>
        <Button disabled={!canManageMembers} variant="contained" onClick={handleDirectInvite}>
          {t("members_invite_direct")}
        </Button>
      </Box>

      <Typography variant="h6" gutterBottom sx={{textShadow: darkMode ? "0 0 6px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px rgb(199, 50, 182), 0 0 24pxrgb(199, 48, 136)",}}>
        {t("members_universal_link")}
      </Typography>

      <Box sx={{ display: "flex", gap: 2, mb: 2 }}>
        <TextField
          disabled={!canManageMembers}
          select
          label={t("members_universal_role")}
          value={universalRole?.id || ""}
          onChange={(e) =>
            setUniversalRole(roles.find((r) => r.id === +e.target.value))
          }
          sx={{ width: 300 }}
        >
          {roles.map((role) => (
            <MenuItem key={role.id} value={role.id}>
              {role.name}
            </MenuItem>
          ))}
        </TextField>
        <Button disabled={!canManageMembers} variant="outlined" onClick={handleUniversalLink}>
           {t("members_create_universal")}
        </Button>
      </Box>

      {universalInvite && (
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <TextField
            value={universalInvite}
            InputProps={{ readOnly: true }}
            sx={{ flexGrow: 1 }}
          />
          <IconButton onClick={() => copyToClipboard(universalInvite)}>
            <ContentCopyIcon />
          </IconButton>
        </Box>
      )}

      <Typography variant="h6" gutterBottom sx={{textShadow: darkMode ? "0 0 6px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px rgb(199, 50, 182), 0 0 24pxrgb(199, 48, 136)",}}>
        {t("members_project_members")}
      </Typography>

      <Table>
        <TableHead>
          <TableRow>
            <TableCell sx={{ width: 200 }}>{t("members_column_username")}</TableCell>
            <TableCell sx={{ width: 150 }}>{t("members_column_firstname")}</TableCell>
            <TableCell sx={{ width: 150 }}>{t("members_column_lastname")}</TableCell>
            <TableCell sx={{ width: 200 }}>{t("members_column_role")}</TableCell>
            <TableCell sx={{ width: 120 }} align="right">{t("members_column_actions")}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {[...members]
            .sort((a, b) => {
              if (a.username === me.username) return -1;
              if (b.username === me.username) return 1;
              return a.username.localeCompare(b.username);
            })
            .map((member) => (
              <TableRow
                key={member.username}
                sx={
                  member.username === me.username
                    ? { backgroundColor: "rgba(153, 50, 204, 0.1)" } // фиолетовый hint (#9932CC с прозрачностью)
                    : {}
                }
              >
                <TableCell sx={{ width: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', }}>
                  {member.username}
                </TableCell>
                <TableCell sx={{ width: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {member.firstName}
                </TableCell>
                <TableCell sx={{ width: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {member.lastName}
                </TableCell>
                <TableCell>
                  <Select
                    value={member.roleId}
                    onChange={(e) => changeRole(member.username, e.target.value)}
                    size="small"
                    disabled={!canManageRoles || member.username === me.username}
                  >
                    {roles.map((role) => (
                      <MenuItem key={role.id} value={role.id}>
                        {role.name}
                      </MenuItem>
                    ))}
                  </Select>
                </TableCell>
                <TableCell align="right">
                  <IconButton
                    color="error"
                    onClick={() => removeUser(member.username)}
                    disabled={!canManageMembers || member.username === me.username}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
        </TableBody>
      </Table>

      <Snackbar
        open={Boolean(copyStatus)}
        message={copyStatus}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      />
    </Box>
  );
};

export default MembersTab;
