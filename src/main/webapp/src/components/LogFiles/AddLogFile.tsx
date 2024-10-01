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

  const [file, setFile] = useState<File | null>(null);

  const handleLogFilePath = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile); // Store the file in state
      setAddLogFileRequest((prev) => ({
        ...prev,
        logFileName: selectedFile.name, // Use the selected file's name
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!file) {
      alert("Please select a log file.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("/logFile/addLogFile", {
        method: "POST",
        body: formData, // Send the file in a FormData object
      });

      const data: LogFileAddedResponse = await response.json();
      setLogFileResponse(data);
    } catch (error) {
      console.error("Error fetching log file:", error);
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
          <label htmlFor="logFilePath">Log File:</label>
          <input
            className="form-control file-input"
            type="file"
            id="logFilePath"
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
          className={`response-message ${
            logFileResponse.logFileAdded ? "success" : "error"
          }`}
        >
          {logFileResponse.message}
        </p>
      )}
    </div>
  );
};
