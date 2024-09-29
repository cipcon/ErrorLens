import React, { useEffect, useState } from "react";
import { Login, LoginResponse } from "./components/Login";
import { Route, Routes, useLocation } from "react-router-dom";
import { ChangePassword } from "./components/ChangePassword";
import { LogEntries } from "./components/LogEntries";
import { Navbar } from "./components/Navbar";
import { MainPage } from "./components/MainPage";
import "./App.css";

const App: React.FC = () => {
  const location = useLocation();
  const [loginResponse, setLoginResponse] = useState<LoginResponse>({
    message: "",
    passwordMatch: false,
    changePassword: "",
  });

  useEffect(() => {
    const stateLoginResponse = location.state?.loginResponse;
    if (stateLoginResponse) {
      setLoginResponse(stateLoginResponse);
    }
  }, [location.state]);

  return (
    <div className="hintergrund">
      <header>
        <Navbar
          changePassword={loginResponse.changePassword}
          message={loginResponse.message}
          passwordMatch={loginResponse.passwordMatch}
        />
      </header>
      <main>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/changePassword" element={<ChangePassword />} />
          <Route path="/logEntries" element={<LogEntries />} />
          <Route
            path="*"
            element={
              <div className="outside">
                <h1>Sie sind außerhalb der Seite</h1>
                <a className="btn btn-primary" href="/login">
                  Zum Einloggen
                </a>
              </div>
            }
          />
        </Routes>
      </main>
    </div>
  );
};

export default App;
