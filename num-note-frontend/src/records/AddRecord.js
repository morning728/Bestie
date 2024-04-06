import React, { useState, useEffect } from 'react'
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { doOrdinaryRequest } from '../jwtLogic/SecurityFunctions.ts';


export default function AddRecord() {
    
    let navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";


    // useEffect(() => {
    //     loadRecord();
    // }, []);

    const [record, setRecord] = useState({
        description: "",
        name: "",
        visibility: ""
    });

    const { description,name, visibility } = record;


    // const loadRecord = async () => {

    //     const url = mainURL + "/api/v1/records/last"
    //     try {
    //         const response = await doOrdinaryRequest(url, null, "get");
    //         // record.weight = response.data.weight;
    //         // record.height = response.data.height;
    //         //console.log(record);
    //         setRecord({...record, "weight" :response.data.weight == 0 ? "": response.data.weight, "height": response.data.height == 0 ? "": response.data.height});
    //     } catch (error) {
    //         if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
    //             navigate("/login");
    //         } else {
    //             console.log(error.message)
    //             //navigate("/error");
    //         }
    //     }
    // };
    
    const onInputChange = (e) => {
        setRecord({...record, [e.target.name] :e.target.value});
    };

    const onSubmit=async(e)=>{
        e.preventDefault();
        const url = mainURL + "/api/v1/projects";
        try {
            const response = await doOrdinaryRequest(url, record, "post");
            navigate("/");
        } catch (error) {
            if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
                navigate("/login");
            } else {
                navigate("/error");
            }
        }
    };
    return (
        <div className='container'>
            <h1>Add Record</h1>
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

                {/* <div className="mb-3">
                    <label htmlFor="weight" className="form-label">Weight</label>
                    <input style={{
                        width: "15%",
                        marginLeft: "42.5%",
                        WebkitAppearance: "none",
                        MozAppearance: "textfield",
                        textAlign: "center"
                    }}
                        name="weight" onChange={(e) => onInputChange(e)} value={weight} type="number" className="form-control" id="weight" aria-describedby="" />
                </div>

                <div className="mb-3">
                    <label htmlFor="height" className="form-label">Height</label>
                    <input style={{
                        width: "15%",
                        marginLeft: "42.5%",
                        WebkitAppearance: "none",
                        MozAppearance: "textfield",
                        textAlign: "center"
                    }}
                        name="height" onChange={(e) => onInputChange(e)} value={height} type="number" className="form-control" id="height" aria-describedby="" />
                </div>

                <div className="mb-3">
                    <label htmlFor="steps" className="form-label">Steps</label>
                    <input style={{
                        width: "15%",
                        marginLeft: "42.5%",
                        WebkitAppearance: "none",
                        MozAppearance: "textfield",
                        textAlign: "center"
                    }}
                        name="steps" onChange={(e) => onInputChange(e)} value={steps} type="number" className="form-control" id="steps" aria-describedby="" />
                </div>

                <div className="mb-3">
                    <label htmlFor="sheets" className="form-label">Sheets</label>
                    <input style={{
                        width: "15%",
                        marginLeft: "42.5%",
                        WebkitAppearance: "none",
                        MozAppearance: "textfield",
                        textAlign: "center"
                    }}
                        name="sheets" onChange={(e) => onInputChange(e)} value={sheets} type="number" className="form-control" id="sheets" aria-describedby=""  />
                </div>

                <div className="mb-3">
                    <label htmlFor="moodMark" className="form-label">Mood Mark</label>
                    <input style={{
                        width: "15%",
                        marginLeft: "42.5%",
                        WebkitAppearance: "none",
                        MozAppearance: "textfield",
                        textAlign: "center"
                    }}
                        name="moodMark" onChange={(e) => onInputChange(e)} value={moodMark} type="number" className="form-control" id="moodMark" aria-describedby="" />
                </div>

                <div className="mb-3">
                    <label htmlFor="mark" className="form-label">Mark</label>
                    <input style={{
                        width: "15%",
                        marginLeft: "42.5%",
                        WebkitAppearance: "none",
                        MozAppearance: "textfield",
                        textAlign: "center"
                    }}
                        name="mark" onChange={(e) => onInputChange(e)} value={mark} type="number" className="form-control" id="mark" aria-describedby="" />
                </div> */}
                <button type="submit" className="btn btn-primary">Create</button>
            </form>

        </div>
    )
}
