import React, { ChangeEvent, FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";

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
  const [loginError, setLoginError] = useState("");
  const navigate = useNavigate();

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
    <>
      <form onSubmit={handleForm} className="login-form">
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
    </>
  );
};
