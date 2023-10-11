import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { removeToken } from '../jwtLogic/SecurityFunctions.ts'

export default function Navbar() {
    let navigate = useNavigate();

    const logout = () => {
        removeToken(); 
        navigate("/login");
    }
    return (
        <div>
            <nav className="navbar bg-body-tertiary navbar-dark bg-primary">
                <div className="container-fluid">
                    <a className="navbar-brand" href="/">
                            Bootstrap
                    </a>
                    <button type="button" className="btn btn-secondary btn-sm" onClick = {()=>logout()}>Logout</button>
                    <Link type="button" className="btn btn-secondary btn-sm" to="/addrecord">Add Record</Link>
                </div>
            </nav>


        </div>
    )
}
