import React, { createContext, useContext } from "react";
import { useProjects } from "../hooks/useProjects";

// 1. Создаем контекст
const ProjectsContext = createContext();

// 2. Провайдер контекста
export const ProjectsProvider = ({ children }) => {
  const projectHook = useProjects(); // используем хук

  return (
    <ProjectsContext.Provider value={projectHook}>
      {children}
    </ProjectsContext.Provider>
  );
};

// 3. Хелпер-хук для удобного доступа
export const useProjectsContext = () => {
  const context = useContext(ProjectsContext);
  if (!context) {
    throw new Error("useProjectsContext должен использоваться внутри <ProjectsProvider>");
  }
  return context;
};
