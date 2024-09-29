import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";
import { LoginResponse } from "./Login";

export const Navbar: React.FC<LoginResponse> = ({
  changePassword,
  passwordMatch,
}) => {
  const navigate = useNavigate();
  const [alertVisibility, setAlertVisibility] = useState<boolean>(false);

  useEffect(() => {
    setAlertVisibility(passwordMatch);
  }, [passwordMatch]);

  const isLoggedIn = localStorage.getItem("isLoggedIn");

  const handleLogout = () => {
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };

  const handlePassword = () => {
    navigate("/changePassword");
  };

  const handleLogs = () => {
    navigate("/logEntries");
  };

  const handleHomePage = () => {
    navigate("/");
  };

  return (
    <>
      {isLoggedIn === "true" ? (
        <div className="nav-padding">
          <nav className="navbar navbar-expand-lg">
            {changePassword && alertVisibility && (
              <>
                <div>{changePassword}</div>
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
                onClick={handleHomePage}
              >
                Hauptseite
              </button>
              <button
                type="button"
                className="btn btn-primary change-password"
                onClick={handleLogs}
              >
                Logeinträge
              </button>
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
