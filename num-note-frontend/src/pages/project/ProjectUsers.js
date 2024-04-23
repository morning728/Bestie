import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faHeart, faHeartBroken, faCat,faCircleUser } from '@fortawesome/free-solid-svg-icons';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';
//import '../../styles/projectUsers/mainPageStyle.css';

const ProjectUsers = () => {
    const { id } = useParams();
    const [users, setUsers] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [project, setProject] = useState({
        id: 0,
        name: "",
        description: "",
        visibility: ""
    });
    const [me, setMe] = useState({
        username: ""
    });
    const [searchResults, setSearchResults] = useState([]);
    const navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";


    useEffect(() => {
        loadUsers();
        loadProject();
        loadMe();
    }, []);

    const loadUsers = async () => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/users`, null, 'get');
            setUsers(response.data);
        } catch (error) {
            handleErrors(error);
        }
    };
    const loadMe = async () => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/users/me`, null, 'get');
            setMe(response.data);
        } catch (error) {
            handleErrors(error);
        }
    };
    const loadProject = async () => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}`, null, "get");
            setProject(response.data);
        } catch (error) {
            handleErrors(error);
        }
    };

    const handleSearch = async () => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/users?contains=${searchTerm}`, null, 'get');
            setSearchResults(response.data.slice(0, 3));
            console.log(1);
        } catch (error) {
            handleErrors(error);
        }
    };

    const handleSearchWithData = async (data) => {
        try {
            if (data != "") {
                const response = await doOrdinaryRequest(`${mainURL}/api/v1/users?contains=${data}`, null, 'get');
                setSearchResults(response.data.slice(0, 3));
            } else {
                setSearchResults([]);
            }
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
    const handleAddToProject = async (userId) => {
        http://localhost:8085/api/v1/projects/38/users?user_id=3
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/users?user_id=${userId}`, null, 'post');
            setUsers(response.data);
        } catch (error) {
            handleErrors(error);
        }
    };
    const handleRemoveUserFromProject = async (userId) => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/users?user_id=${userId}`, null, 'delete');
            setUsers(response.data);
        } catch (error) {
            handleErrors(error);
        }
    };

    return (
        <div className="container" style={{ minHeight: "90vh" }}>
            <h2 className="text-center" style={{ color: "#34d0ba" }}>Пользователи проекта</h2>
            <div className="row justify-content-between align-items-center mb-2">
                <p className="col-auto" style={{ color: "#aaa" }}>Проект: {project.name}</p>
                <div className="col-auto">
                    <div className="input-group" style={{ position: "relative" }}>
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => { setSearchTerm(e.target.value); handleSearchWithData(e.target.value) }}
                            placeholder="Введите имя пользователя"
                            className="form-control"
                            style={{
                                maxWidth: "200px",
                                marginRight: ".3em",
                                borderRadius: "5px",
                                paddingRight: "2.5rem",
                                borderColor: searchTerm ? "#34d0ba" : null,
                                boxShadow: searchTerm && searchTerm.length > 0 ? "0 0 0 0.2rem rgba(52, 208, 186, 0.25)" : "none",
                                transition: "box-shadow 0.7s ease-in" // Добавлен переход для boxShadow
                            }}
                        />
                        {searchTerm !== "" ? (
                            <div style={{ display: "flex", alignItems: "center" }}>
                                <span className="input-group-text" style={{ backgroundColor: "transparent", border: "none", cursor: "pointer", color: "#34d0ba" }} onClick={() => {handleSearchWithData("");setSearchTerm("");}}>
                                    <FontAwesomeIcon icon={faTimes} style={{ fontSize: "1.35em" }} />
                                </span>
                            </div>
                        ) : (
                            <div style={{ display: "flex", alignItems: "center" }}>
                                <span className="input-group-text" style={{ backgroundColor: "transparent", border: "none", cursor: "pointer", color: "red" }}>
                                    <FontAwesomeIcon icon={faHeart} style={{ fontSize: "1em" }} />
                                </span>
                            </div>
                        )}
                    </div>
                    {searchResults.length > 0 && (
                        <div className="position-absolute mt-1" style={{ width: "200px", backgroundColor: "rgba(26, 26, 26, 0.95)", color: "#34d0ba", borderRadius: "5px" }}>
                            <ul className="list-group" style={{ marginBottom: "0" }}>
                                {searchResults.map(user => (
                                    <li key={user.id} className="list-group-item" style={{ backgroundColor: "transparent", border: "none", color: "#34d0ba", lineHeight: ".5", overflow: "hidden", display: "flex", justifyContent: "space-between" }}>
                                        <div style={{ flex: "1", overflow: "hidden", textOverflow: "ellipsis", display: "flex", alignItems: "center", cursor: "pointer" }} title={user.username}>{user.username}</div>
                                        {users.find(u => u.id === user.id) ? (
                                            <span className="badge badge-success" style={{ fontWeight: "bold", color: "#34d0ba" }}>✓</span>
                                        ) : (
                                            <button style={{ paddingTop: "0.2rem", paddingBottom: "0.2rem", paddingLeft: "0.4rem", paddingRight: "0.4rem", fontSize: "0.65rem" }} className="btn btn-sm btn-outline-primary" onMouseEnter={(e) => e.target.style.backgroundColor = "#34d0ba"} onMouseLeave={(e) => e.target.style.backgroundColor = "transparent"} onClick={() => handleAddToProject(user.id)}>+</button>)}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            </div>
            <table className="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th scope="col"></th> {/* Пустой заголовок для размещения значка */}
                        <th scope="col">Username</th>
                        <th scope="col">First Name</th>
                        <th scope="col">Last Name</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
                            <td style={{ width: "2rem" }}>
                                {user.username === me.username ? (
                                    <FontAwesomeIcon icon={faCircleUser} style={{ color: "orange" }} />
                                ) : (
                                    <FontAwesomeIcon icon={faHeartBroken} style={{ color: "red" }} onClick={() => handleRemoveUserFromProject(user.id)} />
                                )}
                            </td>
                            <td>{user.username}</td>
                            <td>{user.first_name}</td>
                            <td>{user.last_name}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ProjectUsers;