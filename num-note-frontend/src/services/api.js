// src/services/api.js
import axios from 'axios';

// базовый URL backend (обрати внимание на твой порт 8085)
const apiClient = axios.create({
    baseURL: 'http://localhost:8765',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor для добавления JWT в каждый запрос
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token'); // либо другой способ хранения
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
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





  
