import React, { useState, useEffect } from "react";
import { Logout } from "./Logout";
import { LoginResponse } from "./Login";
import { useLocation } from "react-router-dom";

export const MainPage = () => {
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
    <>
      <Logout
        changePassword={loginResponse.changePassword}
        message={loginResponse.message}
        passwordMatch={loginResponse.passwordMatch}
      />
    </>
  );
};
