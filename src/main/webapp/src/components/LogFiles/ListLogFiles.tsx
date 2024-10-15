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

interface LogfileAdded {
  logfileAdded: boolean;
}

export const ListLogFiles: React.FC<LogfileAdded> = ({ logfileAdded }) => {
  const [allLogFiles, setAllLogFiles] = useState<LogFile[]>([]);
  const [deleteResponse, setDeleteResponse] = useState<DeleteResponse | null>(
    null
  );
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchLogFiles();
  }, [logfileAdded]);

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
      `Sind Sie sicher, dass Sie die Logdatei löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.`
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

  if (error) {
    return <div>Error: {error}</div>;
  }

  const handleLogFileClick = (logFile: LogFile) => {
    navigate(`/logfile`, { state: { logFile } });
  };

  return (
    <div>
      <div className="align-text">
        <h3>Logdateien</h3>
        {deleteResponse && (
          <p style={{ color: "green" }}>{deleteResponse.message}</p>
        )}
        {error && <p style={{ color: "red" }}>{error}</p>}
      </div>
      {allLogFiles.length === 0 ? (
        <p>Keine Log-Dateien gefunden.</p>
      ) : (
        <table className="log-files-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Dateiname</th>
              <th>Dateipfad</th>
              <th>Änderungsdatum</th>
              <th>Löschen</th>
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
                <td>{new Date(logFile.changed).toLocaleString()}</td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => deleteLogFile(logFile.logFileID)}
                  >
                    Löschen
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
