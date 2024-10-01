import React from "react";
import { useLocation } from "react-router-dom";

export const LogFile = () => {
  const location = useLocation();
  const logFileID = location.state?.logFileID;

  return <div>Id of the LogFile: {logFileID}</div>;
};
