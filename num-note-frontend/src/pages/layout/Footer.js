import React from 'react';
import '../../styles/footer.css';

const Footer = () => {
    return (
        <footer className="footer">
            <div className="container">
                <div className="row">
                    <div className="col-md-12">
                        <p className="text-center" style={{ color: '#34d0ba', fontFamily: 'Kurachka Lapkoi' }}>
                            © 2024 All rights reserved.
                        </p>
                    </div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;