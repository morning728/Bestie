// context/ProjectAccessContext.js
import { createContext, useContext, useEffect, useState } from "react";
import { getMe, getMyRole } from "../services/api";

const ProjectAccessContext = createContext();

export const ProjectAccessProvider = ({ projectId, children }) => {
  const [me, setMe] = useState(null);
  const [myRole, setMyRole] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (projectId) {
      loadAccess();
    }
  }, [projectId]);

  const loadAccess = async () => {
    setLoading(true);
    try {
      const [meRes, roleRes] = await Promise.all([
        getMe(),
        getMyRole(projectId),
      ]);
      setMe(meRes.data);
      setMyRole(roleRes.data);
    } catch (e) {
      console.error("Ошибка загрузки данных пользователя/роли", e);
    } finally {
      setLoading(false);
    }
  };

  const hasPermission = (perm) =>
    !!myRole?.permissionsJson?.[perm];

  return (
    <ProjectAccessContext.Provider
      value={{ me, myRole, hasPermission, loading }}
    >
      {children}
    </ProjectAccessContext.Provider>
  );
};

export const useProjectAccess = () => useContext(ProjectAccessContext);
