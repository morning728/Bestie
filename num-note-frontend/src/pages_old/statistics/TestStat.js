import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';




export default function TestStat() {

    let navigate = useNavigate();
    const mainURL = process.env.REACT_APP_API_URL != null ? process.env.REACT_APP_API_URL : "http://localhost:8765";



    const [genStat, setGenStat] = useState({
        weight: {},
        weightDifferencePerDay: {},
        sheetsPerDay: {},
        stepsPerDay: {},
        extraResults: {
            bestStepsDay: {},
            worstStepsDay: {},
            worstSheetsDay: {},
            bestSheetsDay: {}
        }
    });

    
    useEffect(() => {
        loadGeneralStat();
    }, []);

    const loadGeneralStat = async () => {
        const url = mainURL + `/statistics/goal`;
        try {
            const result = await doOrdinaryRequest(url, null, "get");
            if(result.data.errorMessage != null){
                navigate("/error", { state: result.data.errorMessage });
            }
            setGenStat(result.data);
        } catch (error) {
            if (error.message === 'invalid token' || error.message === 'Request failed with status code 500') {
                navigate("/login");
            } else {
                //console.log(error.message)
                navigate("/error");
            }
        }
    }

    return (
        <div className='container'>
            <h1>TestStat</h1>
            Weight now: {genStat.weight["first"]}
            <br/>
            Weight goal: {genStat.weight["second"]}
            <br/><br/>
            Weight difference per day now: {genStat.weightDifferencePerDay["first"]}<br/>
            Weight difference per day goal: {genStat.weightDifferencePerDay["second"]} <br/><br/>
            Sheets per day now: {genStat.sheetsPerDay["first"]}<br/>
            Sheets per day goal: {genStat.sheetsPerDay["second"]}<br/><br/>
            Steps per day now: {genStat.stepsPerDay["first"]}<br/>
            Steps per day goal: {genStat.stepsPerDay["second"]}<br/><br/><br/><br/><br/><br/>
            The best result in walking: {genStat.extraResults.bestStepsDay["first"]} steps in a {genStat.extraResults.bestStepsDay["second"]}<br/>
            The worst result in walking: {genStat.extraResults.worstStepsDay["first"]} steps in a {genStat.extraResults.worstStepsDay["second"]}<br/>
            The best result in reading: {genStat.extraResults.bestSheetsDay["first"]} sheets in a {genStat.extraResults.bestSheetsDay["second"]}<br/>
            The worst result in reading: {genStat.extraResults.worstSheetsDay["first"]} sheets in a {genStat.extraResults.worstSheetsDay["second"]}<br/>
        </div>
    )
}
