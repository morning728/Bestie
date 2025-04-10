import { useParams } from "react-router-dom";
import { ProjectAccessProvider } from "./ProjectAccessContext";

const ProjectAccessWrapper = ({ children }) => {
  const { projectId } = useParams();
  return (
    <ProjectAccessProvider projectId={projectId}>
      {children}
    </ProjectAccessProvider>
  );
};

export default ProjectAccessWrapper;
