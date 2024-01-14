import './App.css';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Navbar from './layout/Navbar';
import Home from './pages/Home';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import AddRecord from './records/AddRecord';
import EditRecord from './records/EditRecord';
import DefaultError from './pages/errorPages/DefaultError';
import Registration from './pages/auth/Registration';
import Login from './pages/auth/Login';
import Profile from './pages/profile/Profile';
import TestStat from './pages/statistics/TestStat';

function App() {
  const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";
  return (
    <div className="App">
      <Router>
        <p>API_URL: {mainURL}</p>
        <Navbar />
        <Routes>
          <Route exact path="/" element={<Home />} />
          <Route exact path="/addrecord" element={<AddRecord />} />
          <Route exact path="/editRecord/:id" element={<EditRecord />} />

          <Route exact path="/stat" element={<TestStat />} />

          <Route exact path="/error" element={<DefaultError />} />

          <Route exact path="/profile" element={<Profile />} />
          <Route exact path="/registration" element={<Registration />} />
          <Route exact path="/login" element={<Login />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
