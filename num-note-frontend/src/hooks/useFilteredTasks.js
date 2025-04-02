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
  showArchived,
  allStatuses
) => {
  return useMemo(() => {
    return tasks.filter((task) => {
      // 👉 Получаем массив имён тегов
      const taskTagNames = task.tags?.map((t) => t.name) || [];

      // 👉 Получаем имя статуса по ID
      const taskStatusName =
        allStatuses.find((status) => status.id === task.statusId)?.name || "";

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

      const matchesTag = filterTag ? taskTagNames.includes(filterTag) : true;
      const matchesStatus = filterStatus ? taskStatusName === filterStatus : true;
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
    allStatuses,
  ]).sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
};
