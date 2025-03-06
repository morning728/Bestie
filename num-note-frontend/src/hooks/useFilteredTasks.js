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


export const useFilteredTasksWithDates = (tasks, filterTag, filterStatus, filterTitle, startDate, endDate, showArchived) => {
  return useMemo(() => {
    return tasks.filter(task => {
      const taskStartDate = new Date(task.start_date);
      const taskEndDate = new Date(task.end_date);

      // Приводим даты к формату без учета времени (чтобы сравнивались только YYYY-MM-DD)
      taskStartDate.setHours(0, 0, 0, 0);
      taskEndDate.setHours(23, 59, 59, 999);

      const selectedStartDate = startDate ? new Date(startDate).setHours(0, 0, 0, 0) : null;
      const selectedEndDate = endDate ? new Date(endDate).setHours(23, 59, 59, 999) : null;

      // Фильтрация по тегу и статусу
      const matchesTag = filterTag ? task.tag === filterTag : true;
      const matchesStatus = filterStatus ? task.status === filterStatus : true;
      const matchesShowArchived = task.is_archived === showArchived;

      // Фильтрация по названию задачи
      const matchesTitle = filterTitle ? task.title.toLowerCase().includes(filterTitle.toLowerCase()) : true;

      // Фильтрация по датам (учет пересечения диапазонов)
      let matchesDate = true;

      if (selectedStartDate && !selectedEndDate) {
        // Если выбран только startDate, проверяем пересечение с этим днем
        matchesDate = taskEndDate >= selectedStartDate;
      } else if (!selectedStartDate && selectedEndDate) {
        // Если выбран только endDate, проверяем, начинается ли задача раньше или в этот день
        matchesDate = taskStartDate <= selectedEndDate;
      } else if (selectedStartDate && selectedEndDate) {
        // Проверяем полное пересечение интервалов
        matchesDate = taskStartDate <= selectedEndDate && taskEndDate >= selectedStartDate;
      }

      return matchesTag && matchesStatus && matchesDate && matchesShowArchived && matchesTitle;
    });
  }, [tasks, filterTag, filterStatus, filterTitle, startDate, endDate, showArchived]);
};

