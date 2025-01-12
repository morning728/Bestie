import { useMemo } from "react";

export const useFilteredTasks = (tasks, filterTag, filterStatus) => {
  return useMemo(() => {
    return tasks.filter(
      (task) =>
        (filterTag ? task.tag === filterTag : true) &&
        (filterStatus ? task.status === filterStatus : true)
    );
  }, [tasks, filterTag, filterStatus]);
};