
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';
import axios from 'axios';
import '../../styles/homeStyle.css';
axios.defaults.timeout = 5000;


export default function Projects() {
    const [projects, setProjects] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [addFormData, setAddFormData] = useState({
        name: "",
        description: "",
        visibility: ""
    });
    const [selectedProject, setSelectedProject] = useState(null);
    const [editFormData, setEditFormData] = useState({
        name: "",
        description: "",
        visibility: ""
    });
    const navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";

    useEffect(() => {
        loadProjects();
    }, []);

    const loadProjects = async () => {
        try {
            const response = await doOrdinaryRequest(`${mainURL}/api/v1/projects`, null, "get");// await axios.get(`${mainURL}/api/v1/projects?search=${searchTerm}`);
            setProjects(response.data);
        } catch (error) {
            handleErrors(error);
        }
    };

    const handleErrors = (error) => {
        if (error.response && error.response.status === 401) {
            navigate("/login");
        } else {
            navigate("/error", { state: { errorMessage: error.response.data.message != null ?  error.response.data.message :  error.message}});
        }
    };

    const deleteProject = async (id) => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${id}`, null, "delete");
            loadProjects();
        } catch (error) {
            handleErrors(error);
        }
    };

    const handleAddInputChange = (e) => {
        setAddFormData({ ...addFormData, [e.target.name]: e.target.value });
    };

    const handleEditInputChange = (e) => {
        setEditFormData({ ...editFormData, [e.target.name]: e.target.value });
    };

    const addProject = async (e) => {
        e.preventDefault();
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects`, addFormData, "post");
            setAddFormData({
                name: "",
                description: "",
                visibility: ""
            });
            loadProjects();
        } catch (error) {
            handleErrors(error);
        }
    };

    const updateProject = async () => {
        try {
            await doOrdinaryRequest(`${mainURL}/api/v1/projects/${selectedProject.id}`, editFormData, "put");
            setEditFormData({
                name: "",
                description: "",
                visibility: ""
            });
            loadProjects();
        } catch (error) {
            handleErrors(error);
        }
    };

    const handleSearch = (e) => {
        setSearchTerm(e.target.value);
    };

    const searchProjects = (e) => {
        e.preventDefault();
        loadProjects();
    };

    const handleProjectClick = (project) => {
        setSelectedProject(project);
        setEditFormData(project);
    };

    return (
        <div className="container-fluid bg-dark text-light py-4" style={{ minHeight: "100vh", textAlign: 'center', fontFamily: "Kurachka Lapkoi" }}>
            <div className="row" style={{ justifyContent: "space-around" }}>
                <div className="col-md-4">
                    <form onSubmit={addProject} className="mb-4">
                        <h2 style={{ color: "#34d0ba" }}>Add Project</h2>
                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Name"
                                name="name"
                                value={addFormData.name}
                                onChange={handleAddInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Description"
                                name="description"
                                value={addFormData.description}
                                onChange={handleAddInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <select
                                className="form-select"
                                name="visibility"
                                value={addFormData.visibility}
                                onChange={handleAddInputChange}
                            >
                                <option value="">Select visibility</option>
                                <option value="OPEN">OPEN</option>
                                <option value="CLOSE">CLOSE</option>
                            </select>
                        </div>
                        <button type="submit" className="btn btn-success" style={{ backgroundColor: "#34d0ba", border: "none" }}>Add Project</button>
                    </form>
                </div>
                <div className="col-md-4">
                    <form className="mb-4">
                        <h2 style={{ color: "#34d0ba" }}>Update Project</h2>
                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Name"
                                name="name"
                                value={editFormData.name}
                                onChange={handleEditInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Description"
                                name="description"
                                value={editFormData.description}
                                onChange={handleEditInputChange}
                            />
                        </div>
                        <div className="mb-3">
                            <select
                                className="form-select"
                                name="visibility"
                                value={editFormData.visibility}
                                onChange={handleEditInputChange}
                            >
                                <option value="">Select visibility</option>
                                <option value="OPEN">OPEN</option>
                                <option value="CLOSE">CLOSE</option>
                            </select>
                        </div>
                        {selectedProject && (
                            <button type="button" className="btn btn-primary" onClick={updateProject} style={{ backgroundColor: "#34d0ba", border: "none" }}>Update Project</button>)}
                    </form>
                </div>
                <div className="col-md-6">
                    <form onSubmit={searchProjects} className="mb-4">
                        <h2 style={{ color: "#34d0ba" }}>Search Projects</h2>
                        <div className="input-group">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Search projects"
                                value={searchTerm}
                                onChange={handleSearch}
                            />
                            <button className="btn btn-primary" type="submit" style={{ backgroundColor: "#34d0ba", border: "none" }}>Search</button>
                        </div>
                    </form>
                </div>
            </div>
            <div className="table-responsive">
                <table className="table table-dark table-striped">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col" >Name</th>
                            <th scope="col" >Description</th>
                            <th scope="col" >Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {projects.map((project, index) => (
                            <tr key={index} onClick={() => handleProjectClick(project)} style={{ cursor: "pointer" }}>
                                <th scope="row">{index + 1}</th>
                                <td>{project.name}</td>
                                <td>{project.description}</td>
                                <td>
                                    <button className="btn btn-danger me-2" onClick={() => deleteProject(project.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}


// import React, { useEffect, useState } from 'react'
// import axios from "axios";
// import { Link, useNavigate, useSearchParams } from "react-router-dom";

// export default function Home() {
//     const [records, setRecords] = useState([]);
//     let navigate = useNavigate();

//     const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";


//     const [searchParams, setSearchParams] = useSearchParams();
//     const [findParam, setFindParam] = useState("");

//     useEffect(() => {
//         loadRecordsDefault();
//     }, []);

//     const loadRecordsDefault = async () => {
//         if (searchParams.get("findRecord") != null) {
//             loadRecordsWithParams();
//             return;
//         }
//         const url =    findParam == "" ?   mainURL + "/api/v1/projects"
//           : mainURL + `/api/v1/records?findRecord=${findParam}`;
//         try {
//             const response = await doOrdinaryRequest(url, null, "get");
//             setRecords((response.data));

//         } catch (error) {
//             if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
//                 navigate("/login");
//             } else {
//                 navigate("/error");
//             }
//         }
//     };

//     const loadRecordsWithParams = async () => {
//         const url = `http://localhost:8765/api/v1/records?findRecord=${searchParams.get("findRecord")}`;
//         try {
//             const response = await doOrdinaryRequest(url, null, "get");
//             setRecords((response.data));

//         } catch (error) {
//             if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
//                 navigate("/login");
//             } else {
//                 navigate("/error");
//             }
//         }
//     };



//     const deleteRecord = async (par) => {
//         const url = mainURL + `/api/v1/projects/${par}`;
//         try {
//             await doOrdinaryRequest(url, null, "delete");
//             loadRecordsDefault();
//         } catch (error) {
//             if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
//                 navigate("/login");
//             } else {
//                 navigate("/error");
//             }
//         }
//     }


//     const onInputChange = (e) => {
//         setFindParam(e.target.value);
//     };

//     const onClickForSearchForm = (e) => {
//         e.preventDefault();
//         loadRecordsDefault();
//     };

//     return (
//         <div className='container'>
//             <form style={{"marginTop": "1em"}}>
//                 <div>
//                     <input
//                         type="search"
//                         id="mySearch"
//                         name="findRecord"
//                         placeholder="Search the record…"
//                         onChange={(e) => onInputChange(e)} />
//                     <button onClick={e => onClickForSearchForm(e)}>{findParam == "" ? "Get All" : "Find"}</button>
//                 </div>
//             </form>
//             <div className='py-4'>

//                 <table className="table table-dark table-striped-columns">
//                     <thead>
//                         <tr>
//                             <th scope="col">#</th>
//                             <th scope="col" onClick={() => { setRecords(sortByDateReversed(records)) }}>created</th>
//                             <th scope="col" onClick={() => { setRecords(sortByMark(records)) }}>Mark</th>
//                             <th scope="col">description</th>
//                             <th scope="col">action</th>
//                         </tr>
//                     </thead>
//                     <tbody id='records'>
//                         {
//                             Array.isArray(records) ? records.map((record, index) => (
//                                 <tr>
//                                     <th scope="row" key={index}>{index + 1}</th>
//                                     <td>{record.created_at.toString().substring(0, 10)}</td>
//                                     <td>{record.name}</td>
//                                     <td>{record.description}</td>
//                                     <td>
//                                         <button type="button" className="btn btn-outline-dark" style={{ marginRight: ".5em" }}>View</button>
//                                         <Link type="button" className="btn btn-outline-warning" style={{ marginRight: ".5em" }}
//                                             to={`/editRecord/${record.id}`}>
//                                             Edit
//                                         </Link>
//                                         <button onClick={(e) => { deleteRecord(record.id) }} type="button" className="btn btn-outline-danger">Delete</button>
//                                     </td>
//                                 </tr>
//                             )) : null
//                         }
//                     </tbody>
//                 </table>

//             </div>
//         </div>
//     )
// }
