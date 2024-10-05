import React, { useState } from "react";

interface LogFileAddedResponse {
  message: string;
  logFileAdded: boolean;
}

interface AddLogFileRequest {
  logFileName: string;
  logFilePath: string;
}

export const AddLogFile = () => {
  const [logFileResponse, setLogFileResponse] = useState<LogFileAddedResponse>({
    message: "",
    logFileAdded: false,
  });

  const [addLogFileRequest, setAddLogFileRequest] = useState<AddLogFileRequest>(
    { logFileName: "", logFilePath: "" }
  );

  const handleLogFileName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAddLogFileRequest((prev) => ({
      ...prev,
      logFileName: e.target.value,
    }));
  };

  const handleLogFilePath = (e: React.ChangeEvent<HTMLInputElement>) => {
    setAddLogFileRequest((prev) => ({
      ...prev,
      logFilePath: e.target.value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await fetch("/logFile/addLogFile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(addLogFileRequest),
      });

      const data: LogFileAddedResponse = await response.json();
      setLogFileResponse(data);
    } catch (error) {
      console.error("Error adding log file:", error);
      setLogFileResponse({
        message: "Error adding log file. Please try again.",
        logFileAdded: false,
      });
    }
  };

  return (
    <div className="add-log-file-form">
      <h3>Add Log File</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="logFileName">Log File Name:</label>
          <input
            className="form-control"
            type="text"
            id="logFileName"
            value={addLogFileRequest.logFileName}
            onChange={handleLogFileName}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="logFilePath">Log File Path:</label>
          <input
            className="form-control"
            type="text"
            id="logFilePath"
            value={addLogFileRequest.logFilePath}
            onChange={handleLogFilePath}
            required
          />
        </div>

        <button type="submit" className="submit-button">
          Add Log File
        </button>
      </form>
      {logFileResponse.message && (
        <p
          className="response-message"
          style={
            logFileResponse.logFileAdded
              ? { backgroundColor: "#90EE90", color: "#FFF" }
              : { backgroundColor: "#556B2F", color: "#FFF" }
          }
        >
          {logFileResponse.message}
        </p>
      )}
    </div>
  );
};
