import React, { useState } from 'react'
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { setToken } from '../../jwtLogic/SecurityFunctions.ts';


export default function Login() {

    let navigate = useNavigate();

    const [user, setUser] = useState({
        username: "",
        password: "",
    });

    const { username, password } = user;

    const onInputChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        try {
            const { data: response } = await axios.post("http://localhost:8765/security/v1/auth/authenticate", user);
            //console.log(response.access_token);
            setToken(response.access_token);
            navigate("/");
        } catch (error) {
            navigate("/error");
        }
    };
    return (
        <div className='container'>
            <h1>Login</h1>
            <form onSubmit={(e) => onSubmit(e)}>
                <div className="mb-3">
                    <label htmlFor="exampleInputUsername" className="form-label">Кто ты, воин?</label>
                    <input name="username" onChange={(e) => onInputChange(e)} value={username} type="text" className="form-control" id="exampleInputUsername" aria-describedby="" />
                </div>
                <div className="mb-3">
                    <label htmlFor="exampleInputPassword1" className="form-label">password</label>
                    <input name="password" onChange={(e) => onInputChange(e)} value={password} type="text" className="form-control" id="exampleInputPassword1" />
                </div>
                <button type="submit" className="btn btn-primary">Login</button>
            </form>

        </div>
    )
}
