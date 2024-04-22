import './App.css';
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Navbar from './pages/layout/Navbar';
import Home from './pages/project/Projects';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import AddRecord from './pages/records/AddRecord';
import EditRecord from './pages/records/EditRecord';
import DefaultError from './pages/errorPages/DefaultError';
import Registration from './pages/auth/Registration';
import Login from './pages/auth/Login';
import Profile from './pages/profile/Profile';
import TestStat from './pages/statistics/TestStat';
import Footer from './pages/layout/Footer';
import MainProjectPage from './pages/project/MainProjectPage';
import ProjectUsers from './pages/project/ProjectUsers';
import Projects from './pages/project/Projects';

function App() {
  const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";
  return (
    <div className="App">
      <Router>
        {/* <p>API_URL: {mainURL}</p> */}
        <Navbar />
        <Routes>
          <Route exact path="/projects" element={<Projects />} />
          <Route exact path="/projects/:id" element={<MainProjectPage />} />
          <Route exact path="/projects/:id/users" element={<ProjectUsers />} />
          {/* <Route exact path="/addrecord" element={<AddRecord />} />
          <Route exact path="/editRecord/:id" element={<EditRecord />} /> */}

          {/* <Route exact path="/stat" element={<TestStat />} /> */}

          <Route exact path="/error" element={<DefaultError />} />

          <Route exact path="/profile" element={<Profile />} />
          <Route exact path="/registration" element={<Registration />} />
          <Route exact path="/login" element={<Login />} />
        </Routes>
        <Footer />
      </Router>
    </div>
  );
}

export default App;
