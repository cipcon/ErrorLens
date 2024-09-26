import React, { useEffect } from "react";
import { Login } from "./components/Login";
import { Route, Routes, useNavigate } from "react-router-dom";
import { Logout } from "./components/Logout";
import { MainPage } from "./components/MainPage";

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
          <Route path="*" element={<h1>You are out of the page</h1>} />
        </Routes>
      </main>
    </>
  );
};

export default App;
