import React, { ChangeEvent, FormEvent, useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ChangePasswordResponse } from "./ChangePassword";

export interface LoginResponse {
  message: string;
  passwordMatch: boolean;
  changePassword: string;
}

export const Login = () => {
  const [password, setPassword] = useState<string>("");
  const [loginResponse, setLoginResponse] = useState<LoginResponse>({
    message: "",
    passwordMatch: false,
    changePassword: "",
  });
  const [changePasswordResponse, setChangePasswordResponse] =
    useState<ChangePasswordResponse>({ message: "", passwordMatch: false });
  const [loginError, setLoginError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const stateLoginResponse = location.state?.changePasswordResponse;
    if (stateLoginResponse) {
      setChangePasswordResponse(stateLoginResponse);
    }
    // eslint-disable-next-line
  }, []);

  const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
    setPassword(event.target.value);
  };

  const handleForm = async (event: FormEvent) => {
    event.preventDefault();

    try {
      const response = await fetch("/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ password }),
      });

      if (response.ok) {
        const data: LoginResponse = await response.json();
        setLoginResponse(data);

        // Mark the user as logged in
        localStorage.setItem("isLoggedIn", "true");

        // Navigate with the data
        navigate("/", { state: { loginResponse: data } });
      } else {
        setLoginError(
          "Authentifizierung fehlgeschlagen. Bitte überprüfen Sie das Passwort."
        );
      }
    } catch (error) {
      console.error("Error during login:", error);
      setLoginError(
        "Die Anmeldung ist aufgrund eines Netzwerk- oder Serverproblems fehlgeschlagen. Bitte versuchen Sie es erneut."
      );
    }
  };

  return (
    <div className="container">
      <form onSubmit={handleForm} className="login-change-password-form">
        {changePasswordResponse.message && (
          <div>
            {changePasswordResponse.message}
            <br />
            Bitte loggen Sie sich erneut ein
          </div>
        )}

        <h3>Login</h3>
        <div className="form-group">
          <input
            autoFocus
            type="password"
            className="form-control"
            id="inputPassword"
            placeholder="Passwort"
            value={password}
            onChange={handlePasswordChange}
            required
            style={{ borderColor: "black" }}
          />
        </div>
        <button type="submit" className="btn btn-primary btn-login">
          Anmelden
        </button>
        {(loginResponse.message || loginError) && (
          <div className="message">
            <p>{loginResponse.message || loginError}</p>
          </div>
        )}
      </form>
    </div>
  );
};
