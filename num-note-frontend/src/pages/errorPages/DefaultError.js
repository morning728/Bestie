import React, { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';


export default function DefaultError({ navigation, route }) {

    const {state} = useLocation();

    return (
        <div className='container'>
            error: {state != null ? state : "Run away, fast"}
        </div>
    )
}
