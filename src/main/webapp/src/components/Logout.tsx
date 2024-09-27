import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "../App.css";

export const Logout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // Access loginResponse from location.state
  const loginResponse = location.state || {}; // If state is null, use an empty object

  const isLoggedIn = localStorage.getItem("isLoggedIn");

  const handleLogout = () => {
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };

  const handlePassword = () => {
    console.log("Navigating to change password");
    navigate("/changePassword");
  };

  return (
    <>
      {isLoggedIn === "true" ? (
        <div className="nav-padding">
          <nav className="navbar navbar-expand-lg">
            {/* Display changePassword message from loginResponse */}
            <div>{loginResponse.changePassword || "need to fix"}</div>
            <div className="ms-auto">
              <button
                type="button"
                className="btn btn-primary change-password"
                onClick={handlePassword}
              >
                Passwort ändern
              </button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleLogout}
              >
                Ausloggen
              </button>
            </div>
          </nav>
          <hr className="hr" />
        </div>
      ) : (
        <div></div>
      )}
    </>
  );
};
