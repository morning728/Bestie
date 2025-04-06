// hooks/useProjectMembers.js
import { useState, useEffect } from "react";
import {
    getProjectMembers,
    removeUserFromProject,
    addUserToProject,
    changeUserRole,
    generateInvite,
    inviteDirectly,
    generateUniversalLink,
    searchUsers,
    getRolesByProjectId,
    getMe
} from "../services/api";

export const useMembers = (projectId) => {
    const defaultURL = "http://localhost:3000";
    const defaultAccept = defaultURL+"/accept-invite?token=";
    const defaultUniversalAccept = defaultURL+"/accept-universal?token=";
    const [members, setMembers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [searchResults, setSearchResults] = useState([]);
    const [universalInvite, setUniversalInvite] = useState(null);
    const [copyStatus, setCopyStatus] = useState("");

    useEffect(() => {
        if (projectId) {
            loadMembersAndRoles();
        }
    }, [projectId]);

    const loadMembersAndRoles = async () => {
        const [membersRes, rolesRes] = await Promise.all([
            getProjectMembers(projectId),
            getRolesByProjectId(projectId),
        ]);
        setMembers(membersRes.data);
        setRoles(rolesRes.data);
    };


    const searchUsersHandler = async (query) => {
        if (query.length < 3) return setSearchResults([]);
        const { data } = await searchUsers(query);
        const existingUsernames = new Set(members.map((m) => m.username));
        const filtered = data.filter((u) => !existingUsernames.has(u.username));
        setSearchResults(filtered.slice(0, 5));
    };

    const addUser = async (username, roleId, sendInvite = false) => {
        if (sendInvite) {
            await inviteDirectly(projectId, username, roleId);
        } else {
            const { data: token } = await generateInvite(projectId, username, roleId);
            return defaultAccept+token;
        }
        await loadMembersAndRoles();
    };

    const removeUser = async (username) => {
        await removeUserFromProject(projectId, username);
        await loadMembersAndRoles();
    };

    const changeRole = async (username, roleId) => {
        await changeUserRole(projectId, { username, roleId });
        await loadMembersAndRoles();
    };

    const createUniversalLink = async (roleId) => {
        const { data: link } = await generateUniversalLink(projectId, roleId);
        setUniversalInvite(defaultUniversalAccept+link);
    };

    const copyToClipboard = async (text) => {
        await navigator.clipboard.writeText(text);
        setCopyStatus("Ссылка скопирована!");
        setTimeout(() => setCopyStatus(""), 2000);
    };

    return {
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
    };
};
