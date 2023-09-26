import React, { useState } from 'react'
import axios from 'axios';
import { useNavigate } from 'react-router-dom';


export default function AddRecord() {
    
    let navigate = useNavigate();

    const [record, setRecord] = useState({
        username: "",
        description: "",
        income: 0,
    });

    const { username, description, income } = record;

    const onInputChange = (e) => {
        setRecord({...record, [e.target.name] :e.target.value});
    };

    const onSubmit=async(e)=>{
        e.preventDefault();
        await axios.post("http://localhost:8765/numapi/api/v1/records", record);
        navigate("/");
    };
    return (
        <div className='container'>
            <h1>Add Record</h1>
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
                <button type="submit" className="btn btn-primary">Create</button>
            </form>

        </div>
    )
}
