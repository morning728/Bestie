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
import { useProjectAccess } from "../../../context/ProjectAccessContext.js";
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

    const { me, hasPermission, loading } = useProjectAccess();
    const can = hasPermission("CAN_EDIT_PROJECT");

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
        <Box className="roles-tab" sx={{
            color: darkMode ? "#00f6ff" : "#fff",

        }}>
            <Typography variant="h6" gutterBottom sx={{ textShadow: darkMode ? "0 0 6px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px rgb(199, 50, 182), 0 0 24pxrgb(199, 48, 136)", }}>
                Roles
            </Typography>

            <Button variant="contained" onClick={handleNewRole} sx={{ mb: 2 }} disabled={!can}>
                Add Role
            </Button>

            {roles.map((role) => (
                <Paper key={role.id} className="role-item" sx={{ p: 2, mb: 2, backgroundColor: darkMode ? "rgba(255, 255, 255, 0.1)" : "rgba(255, 255, 255, 0.2)", }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="subtitle1">{role.name}</Typography>
                        <Box>
                            <Button onClick={() => {
                                setSelectedRole(role);
                                setRoleName(role.name);
                                setDialogOpen(true);
                                setIsNew(false);
                            }} disabled={!can}>
                                Edit
                            </Button>
                            <IconButton color="error" onClick={() => handleDeleteRole(role.id)} disabled={!can}>
                                <DeleteIcon />
                            </IconButton>
                        </Box>
                    </Box>
                </Paper>
            ))}


            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth PaperProps={{
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
            }}>
                <DialogTitle
                    sx={{
                        color: darkMode ? "#00f6ff" : "#fff",
                        textShadow: darkMode ? "0 0 12px #00f6ff, 0 0 24px #00f6ff" : "0 0 12px #ff90e8, 0 0 24px #ff90e8"

                    }}
                >
                    {isNew ? "Add Role" : "Edit Role"}
                </DialogTitle>
                <DialogContent sx={{
                    color: darkMode ? "#00f6ff" : "#fff",

                }}>
                    <TextField
                        label="Role Name"
                        fullWidth
                        margin="normal"
                        value={roleName}
                        onChange={(e) => setRoleName(e.target.value)}
                        sx={{
                            "& .MuiOutlinedInput-root": {
                                backgroundColor: darkMode ? "rgba(255,255,255,0.1)" : "rgba(0,0,0,0.05)",
                                borderRadius: "8px",
                                "& fieldset": {
                                    borderColor: darkMode ? "#00f6ff" : "#ff69b4",
                                },
                                "&:hover fieldset": {
                                    borderColor: darkMode ? "#00f6ff" : "#ff90e8",
                                },
                                "&.Mui-focused fieldset": {
                                    borderColor: darkMode ? "#00f6ff" : "#ff90e8",
                                    boxShadow: darkMode
                                        ? "0 0 6px #00f6ff, 0 0 12px #00f6ff"
                                        : "0 0 6px #ff90e8, 0 0 12px #ff90e8",
                                },
                            },
                            "& .MuiInputLabel-root": {
                                color: darkMode ? "#aaa" : "#9932cc",
                            },
                            "& .MuiInputLabel-root.Mui-focused": {
                                color: darkMode ? "#00f6ff" : "#ff69b4",
                                textShadow: darkMode ? "0 0 6px #00f6ff" : "0 0 6px #ff90e8",
                            },
                            input: {
                                color: darkMode ? "#fff" : "#2c2c54",
                            },
                        }}
                    />


                    <Typography
                        variant="subtitle1"
                        sx={{
                            mt: 3,
                            mb: 1,
                            fontWeight: 600,
                            color: darkMode ? "#00f6ff" : "#fff",
                            textShadow: darkMode
                                ? "0 0 6px #00f6ff, 0 0 12px #00f6ff"
                                : "0 0 6px #ff90e8, 0 0 12px #ff90e8",
                        }}
                    >
                        Permissions
                    </Typography>

                    <Box
                        sx={{
                            display: "grid",
                            gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))",
                            gap: 2,
                            mt: 1,
                            p: 1,
                            borderRadius: 2,

                            backgroundColor: darkMode ? "rgba(0, 246, 255, 0.05)" : "rgba(255, 255, 255, 0.1)",
                            boxShadow: darkMode
                                ? "0 0 12px rgba(0, 246, 255, 0.2)"
                                : "0 0 12px rgba(255, 105, 180, 0.2)",
                        }}
                    >
                        {availablePermissions.map((perm) => (
                            <FormControlLabel
                                key={perm}
                                control={
                                    <Checkbox
                                        checked={!!selectedRole?.permissionsJson[perm]}
                                        onChange={() => handlePermissionToggle(perm)}
                                        sx={{
                                            color: darkMode ? "#00f6ff" : "#ff69b4",
                                            "&.Mui-checked": {
                                                color: darkMode ? "#00f6ff" : "#ff69b4",
                                            },
                                        }}
                                    />
                                }
                                label={perm.replace(/_/g, " ").toLowerCase().replace(/\b\w/g, (l) => l.toUpperCase())}
                                sx={{
                                    color: darkMode ? "#00f6ff" : "#fff",
                                    textShadow: darkMode
                                        ? "0 0 6px #00f6ff"
                                        : "0 0 6px #ff90e8",
                                }}
                            />
                        ))}
                    </Box>
                </DialogContent>
                <DialogActions
                    sx={{
                        color: darkMode ? "#00f6ff" : "#fff",
                        display: "flex",
                        justifyContent: "flex-end",
                        gap: 2,
                        padding: "1rem 2rem",

                    }}
                >
                    <Button
                        onClick={() => setDialogOpen(false)}
                        sx={{
                            background: darkMode ? "#1b1b40" : "#ffffff33",
                            color: darkMode ? "#00f6ff" : "#ff69b4",
                            border: `1px solid ${darkMode ? "#00f6ff" : "#ff69b4"}`,
                            boxShadow: darkMode
                                ? "0 0 8px #00f6ff, 0 0 16px #00f6ff"
                                : "0 0 8px #ff69b4, 0 0 16px #ff90e8",
                            "&:hover": {
                                backgroundColor: darkMode ? "#00f6ff22" : "#ff69b422",
                            },
                        }}
                    >
                        Cancel
                    </Button>

                    <Button
                        onClick={handleSaveRole}
                        sx={{
                            background: darkMode ? "#00f6ff" : "#ff69b4",
                            color: darkMode ? "#000" : "#fff",
                            boxShadow: darkMode
                                ? "0 0 8px #00f6ff, 0 0 16px #00f6ff"
                                : "0 0 8px #ff69b4, 0 0 16px #ff69b4",
                            "&:hover": {
                                backgroundColor: darkMode ? "#00f6ffcc" : "#ff69b4cc",
                            },
                        }}
                        variant="contained"
                    >
                        Save
                    </Button>
                </DialogActions>

            </Dialog>
        </Box>
    );
};

export default RolesTab;
