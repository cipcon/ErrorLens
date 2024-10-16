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
  setLogfileAdded: React.Dispatch<React.SetStateAction<boolean>>;
}

export const AddLogFile: React.FC<LogfileAdded> = ({ setLogfileAdded }) => {
  const [logFileResponse, setLogFileResponse] = useState<LogFileAddedResponse>({
    message: "",
    changed: false,
  });

  const [addLogFileRequest, setAddLogFileRequest] = useState<AddLogFileRequest>(
    { logFileName: "", logFilePath: "" }
  );
  const [ischanged, setIsChanged] = useState<boolean>(false);

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
      setIsChanged(true);
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
      {logFileResponse.changed && ischanged && (
        <div
          className="alert-center-align alert alert-success alert-dismissible fade show"
          role="alert"
          style={{ marginTop: 20 }}
        >
          <p>{logFileResponse.message}</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setIsChanged(false)}
          ></button>
        </div>
      )}
      {!logFileResponse.changed && ischanged && (
        <div
          className="alert-center-align alert alert-danger alert-dismissible fade show"
          role="alert"
          style={{ marginTop: 20 }}
        >
          <p>{logFileResponse.message}</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setIsChanged(false)}
          ></button>
        </div>
      )}
    </div>
  );
};
