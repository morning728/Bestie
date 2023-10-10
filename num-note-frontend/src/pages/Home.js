import React, { useEffect, useState } from 'react'
import axios from "axios";
import { Link, useParams, useNavigate } from "react-router-dom";
import { doOrdinaryRequest, setToken, getToken, removeToken } from '../jwtLogic/SecurityFunctions.ts';
axios.defaults.timeout = 1000;


export default function Home() {
    const [records, setRecords] = useState([]);
    let navigate = useNavigate();


    useEffect(() => {
        loadRecords();
    }, []);

    const loadRecords = async () => {
        const url = "http://localhost:8765/api/v1/records/sorted?username=user";
        //const result = await axios.get("http://localhost:8765/numapi/api/v1/records/sorted?username=user");
        // result.then(
        //     function (value) {
        //         setRecords(value.data);
        //     },
        //     function (reason) {
        //         navigate("/error");
        //     },
        //   );
        try {
            setToken('eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBZG1pbiIsImlhdCI6MTY5Njk2NTIwMywiZXhwIjoxNjk3MDUxNjAzfQ.kuflRCJCSfCx3aKLMnR4cXE_W-PfZvTXjIGsMmYLXq4');
            const response = await doOrdinaryRequest(url, null, "get"); 
            setRecords(response.data);
        } catch (error) {
            if(error.message == 'invalid token'){
                navigate("/login");
            } else {
                navigate("/error");
            }
        }
    };

    const deleteRecord = async (par)=>{
        try{
            const result = await axios.delete(`http://localhost:8765/api/v1/records/${par}`);
            loadRecords();
        } catch (error){
            navigate("/error");
        }
    }


    return (
        <div className='container'>
            <div className='py-4'>

                <table className="table table-dark table-striped-columns">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">created</th>
                            <th scope="col">username</th>
                            <th scope="col">description</th>
                            <th scope="col">action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            records.map((record, index) => (
                                <tr>
                                    <th scope="row" key={index}>{index + 1}</th>
                                    <td>{record.created.toString().substring(0, 10)}</td>
                                    <td>{record.username}</td>
                                    <td>{record.description}</td>
                                    <td>
                                        <button type="button" className="btn btn-outline-dark" style={{ marginRight: ".5em" }}>View</button>
                                        <Link type="button" className="btn btn-outline-warning" style={{ marginRight: ".5em" }}
                                        to={`/editRecord/${record.id}`}>
                                            Edit
                                        </Link>
                                        <button onClick={(e) => {deleteRecord(record.id)}} type="button" className="btn btn-outline-danger">Delete</button>
                                    </td>
                                </tr>
                            ))
                        }
                    </tbody>
                </table>

            </div>
        </div>
    )
}
