import React, { useEffect, useState } from 'react'
import axios from 'axios';
import { useNavigate, useParams  } from 'react-router-dom';
import { doOrdinaryRequest } from '../jwtLogic/SecurityFunctions.ts';



export default function AddRecord() {
    
    let navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";

    const { id } = useParams();

    const [record, setRecord] = useState({
        id: id,
        description: "",
        name: "",
        visibility: ""
    });


    const {  description,name, visibility } = record;

    const onInputChange = (e) => {
        setRecord({...record, [e.target.name] :e.target.value});
    };

    const onSubmit=async(e)=>{
        e.preventDefault();
        const url = mainURL + `/api/v1/projects/${id}`;
        try {
            const response = await doOrdinaryRequest(url, record, "put");
            navigate("/");
        } catch (error) {
            if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
                navigate("/login");
            } else {
                navigate("/error");
            }
        }
    };

    const loadRecord = async () => {


        const url = mainURL + `/api/v1/projects/${id}`;
        try {
            const response = await doOrdinaryRequest(url, null, "get");
            setRecord(response.data);
        } catch (error) {
            if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
                navigate("/login");
            } else {
                navigate("/error");
            }
        }
    }

    useEffect(() => {
        loadRecord();
      }, []);

    return (
        <div className='container'>
            <h1>Edit Record</h1>
            <form onSubmit={(e)=>onSubmit(e)}>
                <div className="mb-3">
                    <label htmlFor="name" className="form-label">Как ты, воин?</label>
                    <textarea style={{
                        width: "50%",
                        marginLeft: "25%",
                    }} name="name" onChange={(e) => onInputChange(e)} value={name} type="text" className="form-control" id="name" aria-describedby=""/>
                    <div id="emailHelp" className="form-text">We'll never share your secrets with anyone else.</div>
                </div>
                <div className="mb-3">
                    <textarea style={{
                        width: "50%",
                        marginLeft: "25%",
                    }} name="description" onChange={(e) => onInputChange(e)} value={description} type="text" className="form-control" id="description" aria-describedby=""/>
                </div>
                <div className="mb-3">
                    <textarea style={{
                        width: "50%",
                        marginLeft: "25%",
                    }} name="visibility" onChange={(e) => onInputChange(e)} value={visibility} type="text" className="form-control" id="visibility" aria-describedby=""/>
                </div>
                <button type="submit" className="btn btn-primary">Create</button>
            </form>

        </div>
    )
}
