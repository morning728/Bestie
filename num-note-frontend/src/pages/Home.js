import React, { useEffect, useState } from 'react'
import axios from "axios";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { doOrdinaryRequest } from '../jwtLogic/SecurityFunctions.ts';
import { TESTTEST, sortByDate, sortByDateReversed, sortByDescription, sortByDescriptionReversed, sortByMark } from '../HomeLogic/HomeSortFunctions.ts';
axios.defaults.timeout = 5000;


export default function Home() {
    const [records, setRecords] = useState([]);
    let navigate = useNavigate();

    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";


    //const [searchParams, setSearchParams] = useSearchParams();
    const [findParam, setFindParam] = useState("");

    useEffect(() => {
        loadRecordsDefault();
    }, []);

    const loadRecordsDefault = async () => {
        // if (searchParams.get("findRecord") != null) {
        //     loadRecordsWithParams();
        //     return;
        // }
        const url =      mainURL + "/api/v1/projects"                  //findParam == "" ?
         // :
        // mainURL + `/api/v1/records?findRecord=${findParam}`;
        try {
            const response = await doOrdinaryRequest(url, null, "get");
            setRecords((response.data));

        } catch (error) {
            if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
                navigate("/login");
            } else {
                navigate("/error");
            }
        }
    };

    // const loadRecordsWithParams = async () => {
    //     const url = `http://localhost:8765/api/v1/records?findRecord=${searchParams.get("findRecord")}`;
    //     try {
    //         const response = await doOrdinaryRequest(url, null, "get");
    //         setRecords((response.data));

    //     } catch (error) {
    //         if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
    //             navigate("/login");
    //         } else {
    //             navigate("/error");
    //         }
    //     }
    // };



    const deleteRecord = async (par) => {
        const url = mainURL + `/api/v1/projects/${par}`;
        try {
            await doOrdinaryRequest(url, null, "delete");
            loadRecordsDefault();
        } catch (error) {
            if (error.message == 'invalid token' || error.message == 'Request failed with status code 500') {
                navigate("/login");
            } else {
                navigate("/error");
            }
        }
    }


    const onInputChange = (e) => {
        setFindParam(e.target.value);
    };

    const onClickForSearchForm = (e) => {
        e.preventDefault();
        loadRecordsDefault();
    };

    return (
        <div className='container'>
            <form style={{"marginTop": "1em"}}>
                <div>
                    <input
                        type="search"
                        id="mySearch"
                        name="findRecord"
                        placeholder="Search the record…"
                        onChange={(e) => onInputChange(e)} />
                    <button onClick={e => onClickForSearchForm(e)}>{findParam == "" ? "Get All" : "Find"}</button>
                </div>
            </form>
            <div className='py-4'>

                <table className="table table-dark table-striped-columns">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col" onClick={() => { setRecords(sortByDateReversed(records)) }}>created</th>
                            <th scope="col" onClick={() => { setRecords(sortByMark(records)) }}>Mark</th>
                            <th scope="col">description</th>
                            <th scope="col">action</th>
                        </tr>
                    </thead>
                    <tbody id='records'>
                        {
                            Array.isArray(records) ? records.map((record, index) => (
                                <tr>
                                    <th scope="row" key={index}>{index + 1}</th>
                                    <td>{record.created_at.toString().substring(0, 10)}</td>
                                    <td>{record.name}</td>
                                    <td>{record.description}</td>
                                    <td>
                                        <button type="button" className="btn btn-outline-dark" style={{ marginRight: ".5em" }}>View</button>
                                        <Link type="button" className="btn btn-outline-warning" style={{ marginRight: ".5em" }}
                                            to={`/editRecord/${record.id}`}>
                                            Edit
                                        </Link>
                                        <button onClick={(e) => { deleteRecord(record.id) }} type="button" className="btn btn-outline-danger">Delete</button>
                                    </td>
                                </tr>
                            )) : null
                        }
                    </tbody>
                </table>

            </div>
        </div>
    )
}
