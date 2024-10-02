import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";

interface Pattern {
  patternId: number;
  patternName: string;
  pattern: string;
  patternBeschreibung: string;
  schweregrad: string;
  rank: number;
}

interface PatternLogFileRequest {
  logFileID: number;
  patternID: number;
}

export const LogFile = () => {
  const location = useLocation();
  const logFile = location.state?.logFile;
  const [patterns, setPatterns] = useState<Pattern[]>([]);
  const [logFileID, setLogFileID] = useState<number>(0);

  useEffect(() => {
    fetchPatterns();
    setLogFileID(logFile.logFileId);
  }, []);

  const fetchPatterns = async () => {
    try {
      const response = await fetch("/logFilePattern/getPatternsForLogFile", {
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
      setPatterns(data);
    } catch (error) {
      console.error("Error fetching patterns:", error);
    }
  };

  const deletePattern = async (patternId: number) => {
    const patternLogFileRequest: PatternLogFileRequest = {
      logFileID: logFileID,
      patternID: patternId,
    };
    try {
      const response = await fetch("/logFilePattern/deletePatternFromLogFile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(patternLogFileRequest),
      });
      fetchPatterns(); // Refresh the list after deletion
    } catch (error) {
      console.error("Error deleting pattern:", error);
    }
  };

  return (
    <div>
      <h2>LogFile: {logFile.logFileName}</h2>
      <h3>Patterns:</h3>
      <ul>
        {patterns.map((pattern) => (
          <li key={pattern.patternId}>
            {pattern.patternName} - {pattern.pattern}
            <button onClick={() => deletePattern(pattern.patternId)}>
              Delete
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};
