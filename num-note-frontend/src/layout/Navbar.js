import React from 'react'
import { Link } from 'react-router-dom'

export default function Navbar() {
    return (
        <div>
            <nav className="navbar bg-body-tertiary navbar-dark bg-primary">
                <div className="container-fluid">
                    <a className="navbar-brand" href="/">
                            Bootstrap
                    </a>
                    <Link type="button" className="btn btn-secondary btn-sm" to="/addrecord">Add Record</Link>
                </div>
            </nav>


        </div>
    )
}
