import React from 'react';
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faProjectDiagram, faTasks, faUsers } from '@fortawesome/free-solid-svg-icons';
import { Container, Row, Col, Button } from 'react-bootstrap';
import pic1 from '../../static/zanyatost.jpg';
import pic2 from '../../static/1613628308_92-p-fon-dlya-prezent.jpg';




export default function Main() {
    let navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";

    return (
        <div className="container" style={{ color: "#F78F77", minHeight: "100vh", padding: "50px 0" }}>
            <h1 className="text-center mb-4" style={{ color: "#34d0ba" }}>Добро пожаловать в Bestie Project Manager</h1>
            <p className="text-center lead mb-5">Управляйте своими проектами эффективно и просто</p>

            <Container>
                <Row className="text-center mb-4">
                    <Col md={4}>
                        <FontAwesomeIcon icon={faProjectDiagram} size="4x" style={{ color: "#34d0ba" }} />
                        <h3 className="mt-3">Проекты</h3>
                        <p>Создавайте и управляйте своими проектами легко и эффективно.</p>
                    </Col>
                    <Col md={4}>
                        <FontAwesomeIcon icon={faTasks} size="4x" style={{ color: "#34d0ba" }} />
                        <h3 className="mt-3">Задачи</h3>
                        <p>Следите за выполнением задач и соблюдайте сроки.</p>
                    </Col>
                    <Col md={4}>
                        <FontAwesomeIcon icon={faUsers} size="4x" style={{ color: "#34d0ba" }} />
                        <h3 className="mt-3">Команды</h3>
                        <p>Организуйте работу вашей команды для достижения лучших результатов.</p>
                    </Col>
                </Row>

                <Row className="text-center">
                    <Col md={6} className="mb-4">
                        <img src={pic1} alt="Project Management" className="img-fluid rounded" />
                        <h4 className="mt-3">Эффективное управление проектами</h4>
                        <p>Наш сервис помогает вам управлять проектами с максимальной эффективностью.</p>
                    </Col>
                    <Col md={6} className="mb-4">
                        <img src={pic2} alt="Task Management" className="img-fluid rounded" />
                        <h4 className="mt-3">Управление задачами</h4>
                        <p>Контролируйте выполнение задач и достигайте ваших целей вовремя.</p>
                    </Col>
                </Row>

                <Row className="text-center mt-5">
                    <Col>
                        <Button 
                            variant="outline-primary" 
                            size="lg" 
                            style={{ borderColor: "#34d0ba", color: "#34d0ba" }}
                            onMouseEnter={(e) => e.target.style.backgroundColor = "#34d0ba"}
                            onMouseLeave={(e) => e.target.style.backgroundColor = "transparent"}
                            onClick={() => navigate('/registration')}
                        >
                            Начать сейчас
                        </Button>
                    </Col>
                </Row>
            </Container>
        </div>
    );
}
