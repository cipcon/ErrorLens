import React from "react";

export const ChangePassword = () => {
  return (
    <>
      <h2>Change Password</h2>
      <form>
        <label>
          New Password:
          <input type="password" name="password" />
        </label>
        <button type="submit">Change</button>
      </form>
    </>
  );
};
