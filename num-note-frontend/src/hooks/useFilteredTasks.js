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
  filterAssignee,
  filterTitle,
  startDate,
  endDate,
  showArchived,
  allStatuses,
  allMembers
) => {
  return useMemo(() => {
    return tasks.filter((task) => {
      // 👉 Получаем массив имён тегов
      const taskTagNames = task.tags?.map((t) => t.name) || [];
      // 👉 Получаем массив id статусов
      const taskStatusName =
        allStatuses.find((status) => status.id === task.statusId)?.name || "";

      // 👉 Получаем массив id assignees
      const taskAssigneeIds = task.assignees?.map((a) => a.userId) || [];
      // 👉 Получаем имена ответственных по id
      const taskAssigneeNames =
        allMembers.filter((member) => taskAssigneeIds.includes(member.userId))
          .map((member) => member.username);

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
      const matchesAssignee = filterAssignee ? taskAssigneeNames.includes(filterAssignee) : true;
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
        matchesAssignee &&
        matchesDate &&
        matchesShowArchived &&
        matchesTitle
      );
    });
  }, [
    tasks,
    filterTag,
    filterStatus,
    filterAssignee,
    filterTitle,
    startDate,
    endDate,
    showArchived,
    allStatuses,
  ]).sort((a, b) => {
    const dateDiff = new Date(a.startDate) - new Date(b.startDate);
    if (dateDiff !== 0) return dateDiff;
    return a.title.localeCompare(b.title); // замените title на нужное поле с названием
  });
};
