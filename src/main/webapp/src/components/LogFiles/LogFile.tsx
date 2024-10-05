import React, { useState, useEffect, useMemo } from "react";
import { useLocation } from "react-router-dom";
import { AddPatternForm } from "./AddPatternForm";
export interface Pattern {
  patternId: number;
  patternName: string;
  pattern: string;
  patternDescription: string;
  severity: string;
  rank: number;
}

export interface PatternLogFileRequest {
  logFileID: number;
  patternID: number;
  rank: number;
}

export interface MessageResponse {
  message: string;
  changed: boolean;
}

interface UpdatePatternsRanksRequest {
  logFileID: number;
  patterns: Pattern[];
}

export const LogFile = () => {
  const location = useLocation();
  const logFile = location.state?.logFile;
  const [allPatterns, setAllPatterns] = useState<Pattern[]>([]);
  const [patterns, setPatterns] = useState<Pattern[]>([]);
  const [logFileID, setLogFileID] = useState<number>(0);
  const [Message, setMessage] = useState<MessageResponse>({
    message: "",
    changed: false,
  });
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [selectedPattern, setSelectedPattern] = useState<Pattern | null>(null);

  const [selectedPatternId, setSelectedPatternId] = useState<number | null>(
    null
  );
  const newRank =
    patterns.length > 0 ? patterns[patterns.length - 1].rank + 1 : 1;

  const [isAddPatternModalOpen, setIsAddPatternModalOpen] = useState(false);

  useEffect(() => {
    setLogFileID(logFile.logFileID);
  }, []);

  useEffect(() => {
    fetchLogFilePatterns();
    listPatterns();
  }, [logFileID]);

  const handlePatternSelect = (pattern: Pattern, patternId: number) => {
    setSelectedPattern(pattern);
    setSelectedPatternId(patternId);
  };

  const addPatternToLogFile = async () => {
    if (!selectedPattern) return;

    const addPatternRequest: PatternLogFileRequest = {
      logFileID: logFileID,
      patternID: selectedPattern.patternId,
      rank: newRank,
    };

    console.log("addPatternRequest", addPatternRequest);

    try {
      const response = await fetch("/logFilePattern/addPatternToLogFile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(addPatternRequest),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setMessage(data);
      fetchLogFilePatterns(); // Refresh the list after addition
      setSelectedPattern(null);
      setSearchTerm("");
    } catch (error) {
      console.error("Error adding pattern:", error);
      setMessage({ message: "Error adding pattern", changed: true });
    }
  };

  const fetchLogFilePatterns = async () => {
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
      updatePatternRanks(data);
    } catch (error) {
      console.error("Error fetching patterns:", error);
    }
  };

  const listPatterns = async () => {
    try {
      const response = await fetch("/pattern/listPatterns");
      const data = await response.json();
      setAllPatterns(data);
    } catch (error) {
      console.error("Error listing patterns:", error);
    }
  };

  const updatePatternRanks = async (patternsToUpdate: Pattern[]) => {
    const updatedPatterns = patternsToUpdate.map((pattern, index) => ({
      ...pattern,
      rank: index + 1,
    }));

    const updatePatternsRanksRequest: UpdatePatternsRanksRequest = {
      logFileID: logFileID,
      patterns: updatedPatterns,
    };

    try {
      const response = await fetch("/logFilePattern/updatePatternRanks", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(updatePatternsRanksRequest),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      setPatterns(updatedPatterns);
    } catch (error) {
      console.error("Error updating pattern ranks:", error);
    }
  };

  const deletePattern = async (patternId: number) => {
    const deletePatternFromLogFile: PatternLogFileRequest = {
      logFileID: logFileID,
      patternID: patternId,
      rank: 0,
    };
    try {
      const response = await fetch("/logFilePattern/deletePatternFromLogFile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(deletePatternFromLogFile),
      });
      const data = await response.json();
      setMessage(data);
      fetchLogFilePatterns(); // Refresh the list after deletion
    } catch (error) {
      console.error("Error deleting pattern:", error);
    }
  };

  const filteredPatterns = useMemo(() => {
    return allPatterns.filter((pattern) =>
      pattern.patternName.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [allPatterns, searchTerm]);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const openAddPatternModal = () => {
    setIsAddPatternModalOpen(true);
    console.log("Opening modal"); // Add this line for debugging
  };

  return (
    <div>
      <h3>LogFile: {logFile.logFileName}</h3>
      <div className="search-container">
        <input
          className="form-control"
          type="text"
          placeholder="Search patterns..."
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <button
          className="btn btn-primary"
          onClick={addPatternToLogFile}
          disabled={!selectedPattern}
        >
          Add Pattern
        </button>
        <button
          className="btn btn-secondary ml-2"
          onClick={openAddPatternModal}
        >
          Create New Pattern
        </button>
      </div>

      {/* Dropdown for filtered patterns */}
      {searchTerm && (
        <ul className="pattern-dropdown">
          {filteredPatterns.map((pattern) => (
            <li
              className="pattern-dropdown-item"
              style={{
                backgroundColor:
                  selectedPatternId === pattern.patternId
                    ? "#fff"
                    : "transparent",
              }}
              key={pattern.patternId}
              onClick={() => handlePatternSelect(pattern, pattern.patternId)}
            >
              {pattern.patternName} - {pattern.pattern} -{" "}
              {pattern.patternDescription} - {pattern.severity}
            </li>
          ))}
        </ul>
      )}

      <h3>Patterns:</h3>
      {Message && <p>{Message.message}</p>}
      {patterns.length === 0 ? (
        <p>No patterns found.</p>
      ) : (
        <table className="log-files-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Pattern Name</th>
              <th>Pattern</th>
              <th>Beschreibung</th>
              <th>Schweregrad</th>
              <th>Delete</th>
            </tr>
          </thead>
          <tbody>
            {patterns.map((pattern) => (
              <tr key={pattern.patternId}>
                <td>{pattern.rank}</td>
                <td>{pattern.patternName}</td>
                <td>{pattern.pattern}</td>
                <td>{pattern.patternDescription}</td>
                <td>{pattern.severity}</td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => deletePattern(pattern.patternId)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {isAddPatternModalOpen && (
        <AddPatternForm
          onClose={() => {
            setIsAddPatternModalOpen(false);
          }}
          onPatternAdded={() => {
            setIsAddPatternModalOpen(false);
            listPatterns();
          }}
          logFileID={logFileID}
          newRank={newRank}
        />
      )}
    </div>
  );
};
