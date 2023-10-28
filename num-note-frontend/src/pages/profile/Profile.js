import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';




export default function Profile() {

    let navigate = useNavigate();

    const [profile, setProfile] = useState({
        username: null,
        firstName: null,
        lastName: null,
        birthday: null,
        weight: null,
        height: null,
        averageMark: null,
        averageMoodMark: null,
        averageStepsPerDay: null,
        averageSheetsPerDay: null,
        averageIncomePerDay: null,
        averageSymbolsPerDescription: null
    });

    const {
        username,
        firstName,
        lastName,
        birthday,
        weight,
        height,
        averageMark,
        averageMoodMark,
        averageStepsPerDay,
        averageSheetsPerDay,
        averageIncomePerDay,
        averageSymbolsPerDescription
    } = profile;

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = async () => {
        const url = `http://localhost:8765/api/v1/profile`;
        try {
            const response = await doOrdinaryRequest(url, null, "get");
            setProfile(response.data);
            console.log(profile);
            console.log(response.data);
        } catch (error) {
            if (error.message === 'invalid token' || error.message === 'Request failed with status code 500') {
                navigate("/login");
            } else {
                navigate("/error");
            }
        }
    }

    return (
        <div className='container'>
            <h1>Profile</h1>
            <div>username: {username}</div>
            <div>firstName: {firstName}</div>
            <div>lastName: {lastName}</div>
            <div>birthday: {birthday}</div>
            <div>weight: {weight}</div>
            <div>height: {height}</div>
            <div>averageMark: {averageMark}</div>
            <div>averageMoodMark: {averageMoodMark}</div>
            <div>averageStepsPerDay: {averageStepsPerDay}</div>
            <div>averageSheetsPerDay: {averageSheetsPerDay}</div>
            <div>averageIncomePerDay: {averageIncomePerDay}</div>
            <div>averageSymbolsPerDescription: {averageSymbolsPerDescription}</div>
        </div>
    )
}
