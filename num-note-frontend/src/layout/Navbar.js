import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { removeToken } from '../jwtLogic/SecurityFunctions.ts'

export default function Navbar() {
    let navigate = useNavigate();

    const logout = () => {
        removeToken();
        navigate("/login");
    }

    // const [findParam, setFindParam] = useState("");

    // const onInputChange = (e) => {
    //     setFindParam(e.target.value);
    // };

    // const onClickForSearchForm = (e) => {
    //     //e.preventDefault();
    //     console.log(findParam);
    //     if(findParam == null){
    //         navigate("/");
    //     }
    //     navigate("/?findRecord=" + findParam);
    // };
    return (
        <div>
            <nav className="navbar bg-body-tertiary navbar-dark bg-primary">
                <div className="container-fluid">
                    <a className="navbar-brand" href="/">
                        Bootstrap
                    </a>
                    {/* <form>
                        <div>
                            <input
                                type="search"
                                id="mySearch"
                                name="findRecord"
                                placeholder="Search the record…"
                                onChange={(e) => onInputChange(e)} />
                            <button onClick={e => onClickForSearchForm(e)}>{findParam == "" ? "Get All" : "Find"}</button>
                        </div>
                    </form> */}
                    <Link type="button" className="btn btn-secondary btn-sm" to="/profile">Profile</Link>
                    <Link type="button" className="btn btn-secondary btn-sm" to="/stat">Statistics</Link>
                    <Link type="button" className="btn btn-secondary btn-sm" to="/addrecord">Add Record</Link>
                    <button type="button" className="btn btn-secondary btn-sm" onClick={() => logout()}>Logout</button>
                </div>
            </nav>


        </div>
    )
}
