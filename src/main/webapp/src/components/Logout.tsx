import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../App.css";

export const Logout = () => {
  const navigate = useNavigate();

  const [changePassword, setChangePassword] = useState<string>("");
  useEffect(() => {
    const needToChangePassword = localStorage.getItem("changePassword");
    if (needToChangePassword) {
      setChangePassword(needToChangePassword);
    }
  }, []);

  const isLoggedIn = localStorage.getItem("isLoggedIn");

  const handleLogout = () => {
    localStorage.removeItem("isLoggedIn");
    navigate("/login");
  };

  console.log(isLoggedIn);
  console.log(changePassword);
  console.warn(changePassword);

  return (
    <>
      {isLoggedIn === "true" ? (
        <div>
          <nav className="navbar navbar-expand-lg">
            <button
              type="button"
              className="btn btn-primary ms-auto logout-button"
              onClick={handleLogout}
            >
              Ausloggen
            </button>
          </nav>
          <hr className="hr" />
        </div>
      ) : (
        <div></div>
      )}
    </>
  );
};
