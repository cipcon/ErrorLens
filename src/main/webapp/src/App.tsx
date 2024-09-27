import React, { useEffect } from "react";
import { Login } from "./components/Login";
import { Route, Routes, useNavigate } from "react-router-dom";
import { MainPage } from "./components/MainPage";
import { ChangePassword } from "./components/ChangePassword";

const App: React.FC = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const isLoggedIn = localStorage.getItem("isLoggedIn");

    if (isLoggedIn === "true") {
      navigate("/");
    } else {
      navigate("/login");
    }
  }, [navigate]);

  return (
    <>
      <main>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/changePassword" element={<ChangePassword />} />
          <Route path="*" element={<h1>Sie sind außerhalb der Seite</h1>} />
        </Routes>
      </main>
    </>
  );
};

export default App;
