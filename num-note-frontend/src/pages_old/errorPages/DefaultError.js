import React, { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom';
import { doOrdinaryRequest } from '../../jwtLogic/SecurityFunctions.ts';
import '../../styles/mainPagesStyles.css'; // Import styles from the styles.css file

export default function DefaultError() {
    const { state } = useLocation();

    return (
        <div className='container error-container'>
            <p className="error-message">
                Error: {state != null ? state.errorMessage : "Run away, fast"}
            </p>
        </div>
    );
}
