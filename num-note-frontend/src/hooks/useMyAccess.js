// hooks/useProjectMembers.js
import { useState, useEffect } from "react";
import {
    getMe,
    getMyRole
} from "../services/api";

export const useMyAccess = (projectId) => {
    const [me, setMe] = useState(null);
    const [myRole, setMyRole] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (projectId) {
            loadMeAndMyRole();
        }
    }, [projectId]);

    const loadMeAndMyRole = async () => {
        setIsLoading(true);
        try {
            const [meRes, myRoleRes] = await Promise.all([
                getMe(),
                getMyRole(projectId),
            ]);
            setMe(meRes.data);
            setMyRole(myRoleRes.data);
        } catch (err) {
            console.error("Ошибка при получении роли:", err);
        } finally {
            setIsLoading(false);
        }
    };

    const checkMyPermission = (permissionName) => {
        if (!myRole || !myRole.permissionsJson) return null; // ⬅ теперь возвращает null, если ещё не готово
        return !!myRole.permissionsJson[permissionName];
    };

    return {
        checkMyPermission,
        isLoading,
        me,
        myRole,
    };
};


