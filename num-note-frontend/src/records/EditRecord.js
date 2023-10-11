import React, { useEffect, useState } from 'react'
import axios from 'axios';
import { useNavigate, useParams  } from 'react-router-dom';
import { doOrdinaryRequest } from '../jwtLogic/SecurityFunctions.ts';



export default function AddRecord() {
    
    let navigate = useNavigate();

    const [record, setRecord] = useState({
        username: "",
        description: "",
        income: 0,
    });

    const { id } = useParams();

    const { username, description, income } = record;

    const onInputChange = (e) => {
        setRecord({...record, [e.target.name] :e.target.value});
    };

    const onSubmit=async(e)=>{
        e.preventDefault();
        const url = `http://localhost:8765/api/v1/records/${id}`;
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


        const url = `http://localhost:8765/api/v1/records/${id}`;
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
                    <label htmlFor="exampleInputEmail1" className="form-label">Кто ты, воин?</label>
                    <input name="username" onChange={(e) => onInputChange(e)} value={username} type="text" className="form-control" id="exampleInputEmail1" aria-describedby="" />
                    <div id="emailHelp" className="form-text">We'll never share your secrets with anyone else.</div>
                </div>
                <div className="mb-3">
                    <label htmlFor="exampleInputPassword1" className="form-label">Note</label>
                    <input name="description" onChange={(e) => onInputChange(e)} value={description} type="text" className="form-control" id="exampleInputPassword1" />
                </div>
                <div className="mb-3">
                    <label htmlFor="income" className="form-label">Income</label>
                    <input style={{
                        width: "15%",
                        marginLeft: "42.5%",
                        WebkitAppearance: "none",
                        MozAppearance: "textfield"
                    }}
                        name="income" onChange={(e) => onInputChange(e)} value={income} type="number" className="form-control" id="income" aria-describedby="" />
                </div>
                <button type="submit" className="btn btn-primary">Edit</button>
            </form>

        </div>
    )
}
