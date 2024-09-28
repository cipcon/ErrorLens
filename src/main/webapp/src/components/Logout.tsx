import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import { LoginResponse } from "./Login";

export const Logout: React.FC<LoginResponse> = (loginResponse) => {
  const navigate = useNavigate();
  const [alertVisibility, setAlertVisibility] = useState<boolean>(true);

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
            {loginResponse.passwordMatch && alertVisibility && (
              <>
                <div>{loginResponse.changePassword}</div>
                <button
                  className="btn-close"
                  data-bs-dismiss="alert"
                  aria-label="Close"
                  onClick={() => setAlertVisibility(false)}
                ></button>
              </>
            )}

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
