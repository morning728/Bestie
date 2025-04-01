import { useMemo } from "react";

export const useFilteredTasks = (tasks, filterTag, filterStatus) => {
  return useMemo(() => {
    return tasks.filter((task) => {
      const tagName = task.tag?.name;
      const statusName = task.status?.name;

      return (
        (filterTag ? tagName === filterTag : true) &&
        (filterStatus ? statusName === filterStatus : true)
      );
    });
  }, [tasks, filterTag, filterStatus]);
};



export const useFilteredTasksWithDates = (
  tasks,
  filterTag,
  filterStatus,
  filterTitle,
  startDate,
  endDate,
  showArchived
) => {
  return useMemo(() => {
    return tasks.filter((task) => {
      const tagName = task.tag?.name;
      const statusName = task.status?.name;

      const taskStartDate = new Date(task.startDate);
      const taskEndDate = new Date(task.endDate);

      taskStartDate.setHours(0, 0, 0, 0);
      taskEndDate.setHours(23, 59, 59, 999);

      const selectedStartDate = startDate
        ? new Date(startDate).setHours(0, 0, 0, 0)
        : null;
      const selectedEndDate = endDate
        ? new Date(endDate).setHours(23, 59, 59, 999)
        : null;
      
      const matchesTag = filterTag ? tagName === filterTag : true;
      const matchesStatus = filterStatus ? statusName === filterStatus : true;
      const matchesShowArchived = Boolean(task.isArchived) === showArchived;
      const matchesTitle = filterTitle
        ? task.title.toLowerCase().includes(filterTitle.toLowerCase())
        : true;

      let matchesDate = true;

      if (selectedStartDate && !selectedEndDate) {
        matchesDate = taskEndDate >= selectedStartDate;
      } else if (!selectedStartDate && selectedEndDate) {
        matchesDate = taskStartDate <= selectedEndDate;
      } else if (selectedStartDate && selectedEndDate) {
        matchesDate =
          taskStartDate <= selectedEndDate &&
          taskEndDate >= selectedStartDate;
      }

      return (
        matchesTag &&
        matchesStatus &&
        matchesDate &&
        matchesShowArchived &&
        matchesTitle
      );
    });
  }, [
    tasks,
    filterTag,
    filterStatus,
    filterTitle,
    startDate,
    endDate,
    showArchived,
  ]);
};
