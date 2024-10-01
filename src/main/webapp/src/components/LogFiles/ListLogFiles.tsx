import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./LogFiles.css";

interface AllLogFiles {
  logFileID: number;
  logFileName: string;
  logFilePath: string;
  changed: string;
}

export const ListLogFiles = () => {
  const [allLogFiles, setAllLogFiles] = useState<AllLogFiles[]>([]);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchLogFiles = async () => {
    try {
      const response = await fetch("/logFile/listLogFiles", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });
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

  useEffect(() => {
    fetchLogFiles();
  }, []);

  useEffect(() => {
    console.log(allLogFiles);
  }, [allLogFiles]);

  if (error) {
    return <div>Error: {error}</div>;
  }

  const handleLogFileClick = (logFileID: number) => {
    navigate(`/logfile`, { state: { logFileID: logFileID } });
  };

  return (
    <div>
      <h2>Log Files</h2>
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
            </tr>
          </thead>
          <tbody>
            {allLogFiles.map((logFile, index) => (
              <tr key={logFile.logFileID}>
                <td>{index + 1}</td>
                <td>
                  <p
                    onClick={() => handleLogFileClick(logFile.logFileID)}
                    className="log-files-link"
                    style={{ cursor: "pointer", margin: 0 }}
                  >
                    {logFile.logFileName}
                  </p>
                </td>
                <td>{logFile.logFilePath}</td>
                <td>{new Date(logFile.changed).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};
