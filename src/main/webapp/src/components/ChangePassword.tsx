import React, { useState, ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";

interface ChangePasswordResponse {
  message: string;
  passwordMatch: boolean;
}

export const ChangePassword: React.FC = () => {
  const [newPassword, setNewPassword] = useState<string>("");
  const [changePasswordResponse, setChangePasswordResponse] =
    useState<ChangePasswordResponse>({ message: "", passwordMatch: false });
  const [passwordChanged, setPasswordChanged] = useState<boolean>(false);
  const navigate = useNavigate();

  const handleForm = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      const response = await fetch("/changePassword", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ newPassword }),
      });

      const data: ChangePasswordResponse = await response.json();
      console.log(response);
      if (response.ok) {
        setChangePasswordResponse(data);
        setPasswordChanged(true);
      } else {
        setChangePasswordResponse(data);
      }
    } catch (error) {
      console.error("Error when changing password:", error);
      setChangePasswordResponse({
        message:
          "Die Änderung des Passwortes ist aufgrund eines Netzwerk- oder Serverproblems fehlgeschlagen. Bitte versuchen Sie es erneut.",
        passwordMatch: false,
      });
    }
  };

  const handlePasswordChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNewPassword(event.target.value);
  };

  const handleButton = () => {
    navigate("/");
  };

  return (
    <>
      <nav className="navbar navbar-expand-lg">
        <div className="ms-auto">
          <button
            type="button"
            className="btn btn-primary change-password"
            onClick={handleButton}
          >
            Zur Hauptseite
          </button>
        </div>
      </nav>
      <hr className="hr" />
      {!passwordChanged ? (
        <form onSubmit={handleForm} className="login-form">
          <h3>Password ändern</h3>
          <div className="form-group">
            <input
              autoFocus
              type="password"
              className="form-control change-border-color"
              id="inputPassword"
              placeholder="Neues Passwort"
              value={newPassword}
              onChange={handlePasswordChange}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary btn-login">
            Passwort ändern
          </button>
          <p className="password-change-message">
            {changePasswordResponse.message}
          </p>
        </form>
      ) : (
        <div>
          {changePasswordResponse && (
            <div className="message">
              <p>{changePasswordResponse.message}</p>
            </div>
          )}
        </div>
      )}
    </>
  );
};
