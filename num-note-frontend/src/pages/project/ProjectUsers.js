import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';
import '../../styles/projectUsers/mainPageStyle.css';

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
    const [searchResults, setSearchResults] = useState([]);
    const navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";


    useEffect(() => {
        loadUsers();
        loadProject();
    }, []);

    const loadUsers = async () => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}/users`, null, 'get');
            setUsers(response.data);
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
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/users?contains=${data}`, null, 'get');
            setSearchResults(response.data.slice(0, 3));
            console.log(1);
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

    return (
        <div className="container">
            <h2 className="text-center" style={{ color: "#34d0ba" }}>Пользователи проекта</h2>
            <div className="row justify-content-between align-items-center mb-2">
                <p className="col-auto" style={{ color: "#aaa" }}>Проект: {project.name}</p>
                <div className="col">
                    <div className="input-group mb-2">
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => { setSearchTerm(e.target.value); handleSearchWithData(e.target.value) }}
                            placeholder="Введите имя пользователя"
                            className="form-control"
                        />
                        <div className="input-group-append">
                            <button onClick={handleSearch} className="btn btn-primary">Искать</button>
                        </div>
                    </div>
                    {searchResults.length > 0 && (
                        <div className="position-relative">
                            <ul className="list-group">
                                {searchResults.map(user => (
                                    <li key={user.id} className="list-group-item">{user.username} ({user.first_name} {user.last_name})</li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            </div>
            <table className="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th scope="col">Username</th>
                        <th scope="col">First Name</th>
                        <th scope="col">Last Name</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
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