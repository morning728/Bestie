import React, { useState } from 'react'
import axios from 'axios';
import {Link,  useNavigate } from 'react-router-dom';
import { setToken } from '../../jwtLogic/SecurityFunctions.ts';
import '../../styles/mainPagesStyles.css'; // Import styles from the styles.css file

export default function Registration() {
    let navigate = useNavigate();

    // Определение базового URL для запросов к API
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";

    // Состояние пользователя с полями: username, email, password и role
    const [user, setUser] = useState({
        username: "",
        email: "",
        password: "",
        role: "USER"
    });

    // Деструктуризация состояния пользователя
    const { username, email, password, role } = user;

    // Обработчик изменения значений в полях ввода
    const onInputChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    // Обработчик отправки формы
    const onSubmit = async (e) => {
        e.preventDefault();
        try {
            // Отправка POST запроса на регистрацию пользователя
            const { data: response } = await axios.post(mainURL + "/security/v1/auth/register", user);
            // Успешная регистрация пользователя
            setToken(response.access_token);
            navigate("/");
        } catch (error) {
            // Обработка ошибок
            navigate("/error", { state: { errorMessage: error.response.data.message != null ?  error.response.data.message :  error.message}});
        }
    };

    return (
        <div className='dark-bg container d-flex align-items-center justify-content-center' style={{ minHeight: '100vh' }}>
            <div className="login-card">
                {/* Заголовок формы */}
                <h1 className="login-title">Registration</h1>
                {/* Форма регистрации */}
                <form onSubmit={(e) => onSubmit(e)}>
                    {/* Поле ввода для имени пользователя */}
                    <div className="mb-3">
                        <label htmlFor="exampleInputUsername" className="form-label login-label">Username</label>
                        <input name="username" onChange={(e) => onInputChange(e)} value={username} type="text" className="form-control login-input" id="exampleInputUsername" />
                    </div>
                    {/* Поле ввода для email */}
                    <div className="mb-3">
                        <label htmlFor="exampleInputEmail1" className="form-label login-label">Email</label>
                        <input name="email" onChange={(e) => onInputChange(e)} value={email} type="text" className="form-control login-input" id="exampleInputEmail1" />
                    </div>
                    {/* Поле ввода для пароля */}
                    <div className="mb-3">
                        <label htmlFor="exampleInputPassword1" className="form-label login-label">Password</label>
                        <input name="password" onChange={(e) => onInputChange(e)} value={password} type="password" className="form-control login-input" id="exampleInputPassword1" />
                    </div>
                    {/* Кнопка для отправки формы */}
                    <button type="submit" className="btn btn-primary login-button">Register</button>
                    {/* Кнопка для перехода на страницу входа */}
                    <br></br>
                    <Link to="/login" className="btn btn-secondary login-button smaller">Login</Link>
                </form>
            </div>
        </div>
    );
}
// export default function Registration() {

//     let navigate = useNavigate();

//     const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";


//     const [user, setUser] = useState({
//         username: "",
//         email: "",
//         password: "",
//         role: "USER"
//     });

//     const { username, email, password, role } = user;

//     const onInputChange = (e) => {
//         setUser({ ...user, [e.target.name]: e.target.value });
//     };

//     const onSubmit = async (e) => {
//         e.preventDefault();
//         try {
//             const { data: response } = await axios.post(mainURL + "/security/v1/auth/register", user);
//             setToken(response.access_token);
//             //console.log(response.access_token);
//             navigate("/");
//         } catch (error) {
//             //console.log(error.message);
//             navigate("/error");
//         }
//     };
//     return (
//         <div className='container'>
//             <h1>Registration</h1>
//             <form onSubmit={(e) => onSubmit(e)}>
//                 <div className="mb-3">
//                     <label htmlFor="exampleInputUsername" className="form-label">Кто ты, воин?</label>
//                     <input name="username" onChange={(e) => onInputChange(e)} value={username} type="text" className="form-control" id="exampleInputUsername" aria-describedby="" />
//                 </div>
//                 <div className="mb-3">
//                     <label htmlFor="exampleInputEmail1" className="form-label">мыло?</label>
//                     <input name="email" onChange={(e) => onInputChange(e)} value={email} type="text" className="form-control" id="exampleInputEmail1" aria-describedby="" />
//                 </div>
//                 <div className="mb-3">
//                     <label htmlFor="exampleInputPassword1" className="form-label">password</label>
//                     <input name="password" onChange={(e) => onInputChange(e)} value={password} type="text" className="form-control" id="exampleInputPassword1" />
//                 </div>
//                 <button type="submit" className="btn btn-primary">Register</button>
//             </form>

//         </div>
//     )
// }
