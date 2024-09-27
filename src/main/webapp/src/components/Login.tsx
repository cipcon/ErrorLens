import React, { ChangeEvent, FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";

interface LoginResponse {
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

    if (!password) {
      setLoginError("Bitte fügen Sie das Passwort hinzu");
      return;
    }

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

        // Wait until loginResponse is fully updated before navigating
        console.log(data.changePassword);
        navigate("/", { state: data }); // Pass the full loginResponse object
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
      <form onSubmit={handleForm}>
        <div className="row mb-3"></div>
        <div className="row mb-3">
          <label htmlFor="inputPassword" className="col-sm-2 col-form-label">
            Password
          </label>
          <div className="col-sm-10">
            <input
              autoFocus
              type="password"
              className="form-control"
              id="inputPassword"
              value={password}
              onChange={handlePasswordChange}
            />
          </div>
        </div>
        <button type="submit" className="btn btn-primary">
          Sign in
        </button>
        <p>{loginResponse.message ? loginResponse.message : loginError}</p>
      </form>
    </>
  );
};
