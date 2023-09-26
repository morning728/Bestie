import React, { useEffect, useState } from 'react'
import axios from "axios";


export default function Home() {
    const [records, setRecords] = useState([]);


    useEffect(() => {
        loadRecords();
    }, []);

    const loadRecords = async () => {
        const result = await axios.get("http://localhost:8765/numapi/api/v1/records/sorted?username=user");
        setRecords(result.data);
    };


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
                                    <td>{record.created.toString().substring(0,10)}</td>
                                    <td>{record.username}</td>
                                    <td>{record.description}</td>
                                    <td>
                                        <button type="button" className="btn btn-outline-dark" style={{marginRight: ".5em"}}>View</button>
                                        <button type="button" className="btn btn-outline-warning"  style={{marginRight: ".5em"}}>Edit</button>
                                        <button type="button" className="btn btn-outline-danger">Delete</button>
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
