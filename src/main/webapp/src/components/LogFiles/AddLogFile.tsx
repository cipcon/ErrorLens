import React, { useState } from "react";

interface LogFileAddedResponse {
  message: string;
  changed: boolean;
}

interface AddLogFileRequest {
  logFileName: string;
  logFilePath: string;
}

interface LogfileAdded {
  logfileAdded: boolean;
  setLogfileAdded: React.Dispatch<React.SetStateAction<boolean>>;
}

export const AddLogFile: React.FC<LogfileAdded> = ({
  logfileAdded,
  setLogfileAdded,
}) => {
  const [logFileResponse, setLogFileResponse] = useState<LogFileAddedResponse>({
    message: "",
    changed: false,
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
      data.changed && setLogfileAdded(true);
    } catch (error) {
      console.error("Error adding log file:", error);
      setLogFileResponse({
        message: "Error adding log file. Please try again.",
        changed: false,
      });
    }
  };

  return (
    <div className="add-log-file-form">
      <h3>Logdatei hinzufügen</h3>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="logFileName">Logdatei Name:</label>
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
          <label htmlFor="logFilePath">Logdatei Pfad:</label>
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
          Logdatei hinzufügen
        </button>
      </form>
      {logFileResponse.message && (
        <p
          className="response-message"
          style={
            logFileResponse.changed
              ? { backgroundColor: "#687e42", color: "#FFF" }
              : { backgroundColor: "#556B2F", color: "#FFF" }
          }
        >
          {logFileResponse.message}
        </p>
      )}
    </div>
  );
};
