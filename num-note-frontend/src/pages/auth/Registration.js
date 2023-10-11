import React, { useState } from 'react'
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { setToken } from '../../jwtLogic/SecurityFunctions.ts';


export default function Registration() {

    let navigate = useNavigate();

    const [user, setUser] = useState({
        username: "",
        email: "",
        password: "",
        role: "USER"
    });

    const { username, email, password, role } = user;

    const onInputChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        try {
            const { data: response } = await axios.post("http://localhost:8765/security/v1/auth/register", user);
            setToken(response.access_token);
            //console.log(response.access_token);
            navigate("/");
        } catch (error) {
            navigate("/error");
        }
    };
    return (
        <div className='container'>
            <h1>Registration</h1>
            <form onSubmit={(e) => onSubmit(e)}>
                <div className="mb-3">
                    <label htmlFor="exampleInputUsername" className="form-label">Кто ты, воин?</label>
                    <input name="username" onChange={(e) => onInputChange(e)} value={username} type="text" className="form-control" id="exampleInputUsername" aria-describedby="" />
                </div>
                <div className="mb-3">
                    <label htmlFor="exampleInputEmail1" className="form-label">мыло?</label>
                    <input name="email" onChange={(e) => onInputChange(e)} value={email} type="text" className="form-control" id="exampleInputEmail1" aria-describedby="" />
                </div>
                <div className="mb-3">
                    <label htmlFor="exampleInputPassword1" className="form-label">password</label>
                    <input name="password" onChange={(e) => onInputChange(e)} value={password} type="text" className="form-control" id="exampleInputPassword1" />
                </div>
                <button type="submit" className="btn btn-primary">Register</button>
            </form>

        </div>
    )
}
