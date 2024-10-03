import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LogFiles.css";

export interface LogFile {
  logFileID: number;
  logFileName: string;
  logFilePath: string;
  changed: string;
}

interface DeleteResponse {
  changed: boolean;
  message: string;
}

export const ListLogFiles = () => {
  const [allLogFiles, setAllLogFiles] = useState<LogFile[]>([]);
  const [deleteResponse, setDeleteResponse] = useState<DeleteResponse | null>(
    null
  );
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchLogFiles = async () => {
    try {
      const response = await fetch("/logFile/listLogFiles");
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setAllLogFiles(data);
      setError(null);
    } catch (error) {
      console.error("Error fetching log files:", error);
      setError("Failed to fetch log files. Please try again later.");
    }
  };

  const deleteLogFile = async (logFileID: number) => {
    const isConfirmed = window.confirm(
      `Are you sure you want to delete the ingredient? This action cannot be undone.`
    );

    if (!isConfirmed) return;

    try {
      const response = await fetch("/logFile/deleteLogFile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(logFileID),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      if (data.changed) {
        setDeleteResponse(data);
        fetchLogFiles(); // Refresh the list after successful deletion
      } else {
        setError(data.message);
      }
    } catch (error) {
      console.error("Error deleting log file:", error);
      setError("Failed to delete log file. Please try again later.");
    }
  };

  useEffect(() => {
    fetchLogFiles();
  }, []);

  useEffect(() => {
    console.log(allLogFiles);
  }, [allLogFiles]);

  if (error) {
    return <div>Error: {error}</div>;
  }

  const handleLogFileClick = (logFile: LogFile) => {
    navigate(`/logfile`, { state: { logFile } });
  };

  return (
    <div>
      <h2>Log Files</h2>
      {deleteResponse && (
        <div style={{ color: "green" }}>{deleteResponse.message}</div>
      )}
      {error && <div style={{ color: "red" }}>{error}</div>}
      {allLogFiles.length === 0 ? (
        <p>No log files found.</p>
      ) : (
        <table className="log-files-table">
          <thead>
            <tr>
              <th>#</th>
              <th>File Name</th>
              <th>File Path</th>
              <th>Date Changed</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {allLogFiles.map((logFile, index) => (
              <tr key={logFile.logFileID}>
                <td>{index + 1}</td>
                <td>
                  <p
                    onClick={() => handleLogFileClick(logFile)}
                    className="log-files-link"
                    style={{ cursor: "pointer", margin: 0 }}
                  >
                    {logFile.logFileName}
                  </p>
                </td>
                <td>{logFile.logFilePath}</td>
                <td>{new Date(logFile.changed).toLocaleDateString()}</td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => deleteLogFile(logFile.logFileID)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};
