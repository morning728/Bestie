import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';
import '../../styles/mainProjectPage.css';
//import '../../styles/insideProject/mainForMenu.css';
import '../../styles/insideProject/left-nav-style.css';
import '../../styles/insideProject/modalWindow.css';
import '../../styles/insideProject/mainStyles.css';

const MainProjectPage = () => {
    const { id } = useParams();
    const [menuOpen, setMenuOpen] = useState(true);
    const [project, setProject] = useState({
        id: 0,
        name: "",
        description: "",
        visibility: ""
    });
    const [fields, setFields] = useState([]);
    const [newField, setNewField] = useState({
        name: "",
        project_id: id
    });
    const [tasks, setTasks] = useState([]);
    const [editedTask, setEditedTask] = useState(null);
    const [selectedTask, setSelectedTask] = useState(null);
    const navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";


    useEffect(() => {
        loadProject();
    }, []);

    const loadProject = async () => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}`, null, "get");
            setProject(response.data);
            loadFields(response.data.id);
        } catch (error) {
            handleErrors(error);
        }
    };
    const loadFields = async (projectId) => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${projectId}/fields`, null, "get");
            setFields(response.data);
            loadTasksForFields(response.data);
        } catch (error) {
            handleErrors(error);
        }
    };

    const loadTasksForFields = async (fields) => {
        try {
            const tasksPromises = fields.map(async (field) => {
                const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/tasks?field_id=${field.id}`, null, "get");
                return { fieldId: field.id, tasks: response.data };
            });
            const tasksData = await Promise.all(tasksPromises);
            setTasks(tasksData);
        } catch (error) {
            handleErrors(error);
        }
    };

    const handleErrors = (error) => {
        if (error.response && error.response.status === 401) {
            navigate("/login");
        } else {
            navigate("/error", { state: { errorMessage: error.response.data.message != null ? error.response.data.message : error.message } });
        }
    };

    const toggleMenu = () => {
        setMenuOpen(!menuOpen);
    };

    const handleNewFieldChange = (e) => {
        setNewField({ ...newField, name: e.target.value });
    };

    const addField = async () => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/fields`, newField, "post");
            setNewField({
                name: "",
                project_id: id
            }); // Очищаем поле ввода после добавления поля
            loadFields(id); // После добавления поля загружаем обновленный список полей
        } catch (error) {
            handleErrors(error);
        }
    };

    const deleteField = async (fieldId) => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/fields/${fieldId}`, null, "delete");
            loadFields(id); // После удаления поля загружаем обновленный список полей
        } catch (error) {
            handleErrors(error);
        }
    };

    const editField = async (fieldId, newName) => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/fields/${fieldId}`, { id: fieldId, name: newName, project_id: id }, "put");
            loadFields(id); // После редактирования поля загружаем обновленный список полей
        } catch (error) {
            handleErrors(error);
        }
    };

    const addTask = async (fieldId, taskName) => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/tasks`, { name: taskName, project_id: id, field_id: fieldId }, "post");
            loadTasksForFields(fields); // После добавления задачи загружаем обновленные задачи для всех полей
        } catch (error) {
            handleErrors(error);
        }
    };


    // const handleEditTask = async () => {
    //     try {
    //         await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/tasks/${selectedTask.id}`, editedTask, "put");
    //         loadTasksForFields(fields); 
    //         setSelectedTask(null);
    //         setEditedTask(null);
    //     } catch (error) {
    //         handleErrors(error);
    //     }
    // };

    // const handleDeleteTask = async (taskId) => {
    //     try {
    //         await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/tasks/${taskId}`, null, "delete");
    //         loadTasksForFields(fields); 
    //         setSelectedTask(null);
    //     } catch (error) {
    //         handleErrors(error);
    //     }
    // };
    const handleTaskClick = (task) => {
        setSelectedTask(task);
        setEditedTask({ ...task });
    };

    const handleEditTaskChange = (e) => {
        const { name, value } = e.target;
        setEditedTask({ ...editedTask, [name]: value });
    };

    const handleEditTask = async () => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/tasks/${editedTask.id}`, editedTask, "put");
            loadTasksForFields(fields);
            setSelectedTask(null);
        } catch (error) {
            handleErrors(error);
        }
    };

    const handleDeleteTask = async (taskId) => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/tasks/${taskId}`, null, "delete");
            loadTasksForFields(fields);
            setSelectedTask(null);
        } catch (error) {
            handleErrors(error);
        }
    };
    return (
        <div>
            {/* Начало боковой панели */}
            <input type="checkbox" id="nav-toggle" hidden></input>
            <nav className="nav">
                <label for="nav-toggle" className="nav-toggle" onclick></label>
                <div className="project-info">
                    <div className="project-name">{project.name}</div>
                    <div className="project-description">{project.description}</div>
                </div>
                <ul>
                    <li><a href="#1">Основная информация</a></li>
                    <li><a href="#2">Участники</a></li>
                </ul>
            </nav>
            {/* Конец боковой панели */}
            {/* Основная часть компонента */}
            <div className="main-content">
                {fields.map(field => (
                    <div key={field.id} className="field-card">
                        <h3 >{field.name}</h3>
                        <ul>
                            {tasks.find(taskData => taskData.fieldId === field.id)?.tasks.map(task => (
                                <div key={task.id}>
                                    <li onClick={() => handleTaskClick(task)}>{`${task.name}`}</li>
                                    {/* Добавьте другие поля задачи, если необходимо */}
                                </div>
                            ))}
                        </ul>
                        {/* Форма для добавления задачи */}
                        <form onSubmit={(e) => {
                            e.preventDefault();
                            const taskName = e.target.elements.taskName.value;
                            e.target.elements.taskName.value = "";
                            addTask(field.id, taskName);
                        }}>
                            <input type="text" name="taskName" placeholder="Введите название задачи" className='task-input' />
                            <button type="submit" className='add-task-button'>Добавить задачу</button>
                        </form>
                        {/* Конец формы для добавления задачи */}
                        <div style={{ marginTop: "3em" }}>
                            <button className="field-button delete-button" onClick={() => deleteField(field.id)}>Удалить</button>
                            <button className="field-button edit-button" onClick={() => {
                                const newName = prompt("Введите новое имя поля:", field.name);
                                if (newName !== null && newName !== "") {
                                    editField(field.id, newName);
                                }
                            }}>Редактировать</button>
                        </div>

                    </div>
                ))}
                {/* Шаблон для добавления нового поля */}
                <div className="field-card" >
                    <input type="text" value={newField.name} onChange={handleNewFieldChange} style={{ marginBottom: "10px", width: "100%", padding: "5px" }} />
                    <button onClick={addField} className='field-button'>Добавить поле</button>
                </div>
            </div>
            {/* Конец основной части компонента */}
            {/*Модальное окно*/}
            {selectedTask && (
                <div className="modal">
                    <div className="modal-content">
                        <span className="close" onClick={() => setSelectedTask(null)}>&times;</span>
                        <h2>
                            <input
                                type="text"
                                name="name"
                                value={editedTask.name}
                                onChange={handleEditTaskChange}
                                className='task-input-editing'
                            />
                        </h2>
                        <p>
                            <textarea
                                name="description"
                                value={editedTask.description}
                                onChange={handleEditTaskChange}
                                className='task-description'
                            />
                        </p>
                        <p>Created At: {selectedTask.created_at}</p>
                        <p>Updated At: {selectedTask.updated_at}</p>
                        <button onClick={handleEditTask} className='save-button' >Сохранить</button>
                        <button onClick={() => handleDeleteTask(selectedTask.id)} className='delete-button' >Удалить</button>
                    </div>
                </div>
            )}
            {/*конец Модальное окно*/}
        </div>
    );
};

export default MainProjectPage;