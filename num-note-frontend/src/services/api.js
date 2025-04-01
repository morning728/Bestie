// src/services/api.js
import axios from 'axios';

// Создаем инстанс axios
const apiClient = axios.create({
  baseURL: 'http://localhost:8765',
  headers: {
      'Content-Type': 'application/json',
  },
  withCredentials: true, // чтобы работать с cookies (refresh_token)
});

let isRefreshing = false;
let refreshSubscribers = [];

// Функция для подписки запросов, пока `refresh` выполняется
const subscribeTokenRefresh = (callback) => {
    refreshSubscribers.push(callback);
};

// Функция для выполнения всех отложенных запросов
const onRefreshed = (newToken) => {
    refreshSubscribers.map((callback) => callback(newToken));
    refreshSubscribers = [];
};


// Interceptor для добавления JWT в каждый запрос
apiClient.interceptors.request.use(
  (config) => {
      const token = localStorage.getItem('token');
      if (token) {
          config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
  },
  (error) => Promise.reject(error)
);


// 🔄 **Функция для обновления токена** (из твоего кода)
const refreshToken = async () => {
    try {
        const response = await axios.post(
            "http://localhost:8765/security/v1/auth/refresh",
            {},
            { withCredentials: true }
        );

        const newAccessToken = response.data.access_token;
        localStorage.setItem("token", newAccessToken); // Сохраняем новый токен
        return newAccessToken;
    } catch (error) {
        console.error("Ошибка при обновлении токена", error);
        return null; // Если обновить токен не удалось, пользователь должен перелогиниться
    }
};

// **Interceptor** для автоматического обновления токена
apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Если `401 Unauthorized` и это не повторный запрос
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            // Если токен уже обновляется, подписываем запрос на ожидание нового токена
            if (isRefreshing) {
                return new Promise((resolve) => {
                    subscribeTokenRefresh((newToken) => {
                        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
                        resolve(apiClient(originalRequest));
                    });
                });
            }

            isRefreshing = true; // Устанавливаем флаг, что обновление токена началось

            try {
                const newAccessToken = await refreshToken();
                if (!newAccessToken) {
                    return Promise.reject(error); // Если токен не удалось обновить — ошибка
                }

                apiClient.defaults.headers["Authorization"] = `Bearer ${newAccessToken}`;
                onRefreshed(newAccessToken); // Запускаем все отложенные запросы

                // Повторяем запрос с новым токеном
                originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;
                return apiClient(originalRequest);
            } finally {
                isRefreshing = false; // После обновления сбрасываем флаг
            }
        }

        return Promise.reject(error);
    }
);


// AUTH
export const registerUser = (username, password, email, role = 'USER') => 
  apiClient.post('/security/v1/auth/register', { username, password, email, role });

export const loginUser = (username, password) => 
  apiClient.post('/security/v1/auth/authenticate', { username, password });

// PROJECTS
export const getMyProjects = () => apiClient.get('/api/v1/projects/my');

export const createProject = (projectData) => apiClient.post('/api/v1/projects', projectData);

export const updateProject = (projectId, updateProjectDTO) =>
  apiClient.put(`/api/v1/projects/${projectId}`, updateProjectDTO);

export const deleteProject = (projectId) =>
  apiClient.delete(`/api/v1/projects/${projectId}`);

export const getFullProjectInfoById = (projectId) =>
  apiClient.get(`/api/v1/projects/${projectId}/full-info`);

// MEMBERS
export const getProjectMembers = (projectId) =>
  apiClient.get(`/api/v1/projects/${projectId}/users`);

export const removeUserFromProject = (projectId, username) =>
  apiClient.delete(`/api/v1/projects/${projectId}/users/${username}`);

export const addUserToProject = (projectId, userToProjectDTO) =>
  apiClient.post(`/api/v1/projects/${projectId}/users`, userToProjectDTO);

export const changeUserRole = (projectId, userToProjectDTO) =>
  apiClient.put(`/api/v1/projects/${projectId}/users`, userToProjectDTO);

// ROLES
export const getRolesByProjectId = (projectId) =>
  apiClient.get(`/api/v1/projects/${projectId}/roles`);

export const addRole = (projectId, roleData) =>
  apiClient.post(`/api/v1/projects/${projectId}/roles`, roleData);

export const updateRole = (projectId, roleData) =>
  apiClient.put(`/api/v1/projects/${projectId}/roles`, roleData);

export const deleteRole = (projectId, roleId) =>
  apiClient.delete(`/api/v1/projects/${projectId}/roles/${roleId}`);

// TAGS
export const getProjectTags = (projectId) =>
  apiClient.get(`/api/v1/projects/${projectId}/tags`);

export const updateProjectTag = (projectId, tagData) =>
  apiClient.put(`/api/v1/projects/${projectId}/tags`, tagData);

export const addProjectTag = (projectId, tag) =>
  apiClient.post(`/api/v1/projects/${projectId}/tags`, tag);

export const deleteProjectTag = (projectId, tagId) =>
  apiClient.delete(`/api/v1/projects/${projectId}/tags/${tagId}`);

// Statuses
export const getProjectStatuses = (projectId) => 
  apiClient.get(`/api/v1/projects/${projectId}/statuses`);

export const addProjectStatus = (projectId, statusData) => 
  apiClient.post(`/api/v1/projects/${projectId}/statuses`, statusData);

export const updateProjectStatus = (projectId, statusData) =>
   apiClient.put(`/api/v1/projects/${projectId}/statuses`, statusData);

export const deleteProjectStatus = (projectId, statusId) => 
  apiClient.delete(`/api/v1/projects/${projectId}/statuses/${statusId}`);

// 📌 🔥 Методы для работы с ресурсами проекта

// Получение списка ресурсов проекта
export const getProjectResources = (projectId) => 
  apiClient.get(`/api/v1/projects/${projectId}/resources`);

// Добавление нового ресурса
export const addProjectResource = (projectId, resourceData) => 
  apiClient.post(`/api/v1/projects/${projectId}/resources`, resourceData);

// Обновление ресурса
export const updateProjectResource = (projectId, resourceData) => 
  apiClient.put(`/api/v1/projects/${projectId}/resources`, resourceData);

// Удаление ресурса
export const deleteProjectResource = (projectId, resourceId) => 
  apiClient.delete(`/api/v1/projects/${projectId}/resources/${resourceId}`);

// TASKS
export const getTasksByProject = (projectId) =>
  apiClient.get(`/api/v1/tasks/project/${projectId}/all`);

export const createTask = (taskDTO) =>
  apiClient.post(`/api/v1/tasks`, taskDTO);

export const updateTask = (taskId, taskDTO) =>
  apiClient.put(`/api/v1/tasks/${taskId}`, taskDTO);

export const archiveTask = (taskId) =>
  apiClient.put(`/api/v1/tasks/${taskId}/archive`);

export const restoreTask = (taskId) =>
  apiClient.put(`/api/v1/tasks/${taskId}/restore`);





  
