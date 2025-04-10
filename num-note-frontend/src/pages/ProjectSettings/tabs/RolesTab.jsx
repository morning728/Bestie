import React, { useState, useEffect, useContext } from "react";
import {
    Box,
    Typography,
    Paper,
    IconButton,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    FormControlLabel,
    Checkbox,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import SaveIcon from "@mui/icons-material/Save";
import { useTranslation } from "react-i18next";
import { ThemeContext } from "../../../ThemeContext";
import {
    getRolesByProjectId,
    addRole,
    updateRole,
    deleteRole,
} from "../../../services/api";
import "./RolesTab.css";

const availablePermissions = [
    "CAN_CREATE_TASKS",
    "CAN_EDIT_TASKS",
    "CAN_DELETE_TASKS",
    "CAN_ARCHIVE_TASKS",
    "CAN_RESTORE_TASKS",
    "CAN_COMMENT_TASKS",
    "CAN_MANAGE_REMINDERS",
    "CAN_MANAGE_TASK_TAGS",
    "CAN_MANAGE_TASK_STATUSES",
    "CAN_MANAGE_ASSIGNEES",
    "CAN_EDIT_PROJECT",
    "CAN_MANAGE_MEMBERS",
    "CAN_MANAGE_ROLES",
];

const RolesTab = ({ projectId }) => {
    const { darkMode } = useContext(ThemeContext);
    const [roles, setRoles] = useState([]);
    const [selectedRole, setSelectedRole] = useState(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [roleName, setRoleName] = useState("");
    const [isNew, setIsNew] = useState(false);
    const { t } = useTranslation();

    const fetchRoles = () => {
        getRolesByProjectId(projectId).then((res) => {
            const data = res.data.map((role) => ({
                ...role,
                permissionsJson: role.permissionsJson
                    ? role.permissionsJson
                    : JSON.parse(role.permissions),
            }));
            setRoles(data);
        });
    };

    useEffect(() => {
        fetchRoles();
    }, [projectId]);

    const handleRoleClick = (role) => {
        setSelectedRole(role);
        setDialogOpen(true);
        setIsNew(false);
        setRoleName(role.name);
    };

    const handleNewRole = () => {
        setIsNew(true);
        setRoleName("");
        setSelectedRole({ permissionsJson: {} });
        setDialogOpen(true);
    };

    const handlePermissionToggle = (perm) => {
        setSelectedRole((prev) => ({
            ...prev,
            permissionsJson: {
                ...prev.permissionsJson,
                [perm]: !prev.permissionsJson[perm],
            },
        }));
    };

    const handleSaveRole = () => {
        const payload = {
            name: roleName,
            permissionsJson: selectedRole.permissionsJson,
        };

        const request = isNew
            ? addRole(projectId, payload)
            : updateRole(projectId, { ...selectedRole, ...payload });

        request
            .then(() => {
                fetchRoles();
                setDialogOpen(false);
            })
            .catch((err) => console.error("Ошибка сохранения роли:", err));
    };

    const handleDeleteRole = (roleId) => {
        deleteRole(projectId, roleId)
            .then(fetchRoles)
            .catch((err) => console.error("Ошибка удаления роли:", err));
    };

    return (
        <Box className="roles-tab">
            <Typography variant="h6" gutterBottom>
                Roles
            </Typography>

            <Button variant="contained" onClick={handleNewRole} sx={{ mb: 2 }}>
                Add Role
            </Button>

            {roles.map((role) => (
                <Paper key={role.id} className="role-item" sx={{ p: 2, mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="subtitle1">{role.name}</Typography>
                        <Box>
                            <Button onClick={() => {
                                setSelectedRole(role);
                                setRoleName(role.name);
                                setDialogOpen(true);
                                setIsNew(false);
                            }}>
                                Edit
                            </Button>
                            <IconButton color="error" onClick={() => handleDeleteRole(role.id)}>
                                <DeleteIcon />
                            </IconButton>
                        </Box>
                    </Box>
                </Paper>
            ))}


            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth>
                <DialogTitle
                    sx={{
                        backgroundColor: darkMode ? "#2b2b60" : "#9932CC",
                        color: "white",
                    }}
                >
                    {isNew ? "Add Role" : "Edit Role"}
                </DialogTitle>
                <DialogContent>
                    <TextField
                        label="Role Name"
                        fullWidth
                        margin="normal"
                        value={roleName}
                        onChange={(e) => setRoleName(e.target.value)}
                    />

                    <Typography variant="subtitle1" mt={2}>
                        Permissions
                    </Typography>
                    <Box>
                        {availablePermissions.map((perm) => (
                            <FormControlLabel
                                key={perm}
                                control={
                                    <Checkbox
                                        checked={!!selectedRole?.permissionsJson[perm]}
                                        onChange={() => handlePermissionToggle(perm)}
                                    />
                                }
                                label={perm.replace(/_/g, ' ')}
                            />
                        ))}
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
                    <Button
                        onClick={handleSaveRole}
                        variant="contained"
                        color="primary"
                    >
                        Save
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default RolesTab;
