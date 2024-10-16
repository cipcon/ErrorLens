import React, { useState, useEffect, useMemo } from "react";
import { useLocation } from "react-router-dom";
import { AddPatternForm } from "./AddPatternForm";
import { FaArrowUp, FaArrowDown } from "react-icons/fa";

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

export interface AddPatternResponse {
  message: string;
  changed: boolean;
  patternId: number;
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
  const [message, setMessage] = useState<MessageResponse>({
    message: "",
    changed: false,
  });
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [selectedPattern, setSelectedPattern] = useState<Pattern | null>(null);

  const newRank =
    patterns.length > 0 ? patterns[patterns.length - 1].rank + 1 : 1;

  const [isAddPatternModalOpen, setIsAddPatternModalOpen] = useState(false);
  const [patternAdded, setPatternAdded] = useState<AddPatternResponse>({
    message: "",
    changed: false,
    patternId: 0,
  });
  const [isPatternAdded, setIsPatternAdded] = useState<boolean>(false);
  const [isPatternDeleted, setIsPatternDeleted] = useState<boolean>(false);

  useEffect(() => {
    setLogFileID(logFile.logFileID);
  }, [logFile.logFileID]);

  useEffect(() => {
    logFileID && fetchLogFilePatterns();
    // eslint-disable-next-line
  }, [logFileID, patternAdded]);

  useEffect(() => {
    fetchLogFilePatterns();
  }, [patternAdded.changed]);

  const handlePatternSelect = (pattern: Pattern) => {
    setSelectedPattern(pattern);
  };

  const addPatternToLogFile = async () => {
    if (!selectedPattern) return;

    const addPatternRequest: PatternLogFileRequest = {
      logFileID: logFileID,
      patternID: selectedPattern.patternId,
      rank: newRank,
    };

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
      setIsPatternAdded(true);
      fetchLogFilePatterns(); // Refresh the list after addition
      setSelectedPattern(null);
      setSearchTerm("");
    } catch (error) {
      console.error("Error adding pattern:", error);
      setMessage({
        message: "Es ist ein Fehler aufgetreten, bitte versuchen Sie erneut",
        changed: true,
      });
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
    const isConfirmed = window.confirm(
      `Sind Sie sicher, dass Sie den Logeintrag löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.`
    );

    if (!isConfirmed) return;
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
      setIsPatternDeleted(true);
      fetchLogFilePatterns(); // Refresh the list after deletion
    } catch (error) {
      setMessage({
        message: "Es ist ein Fehler aufgetreten. Bitte versuchen Sie erneut",
        changed: true,
      });
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
    listPatterns();
  };

  const openAddPatternModal = () => {
    setIsAddPatternModalOpen(true);
  };

  const movePattern = (index: number, direction: "up" | "down") => {
    const newPatterns = [...patterns];
    if (direction === "up" && index > 0) {
      [newPatterns[index - 1], newPatterns[index]] = [
        newPatterns[index],
        newPatterns[index - 1],
      ];
    } else if (direction === "down" && index < newPatterns.length - 1) {
      [newPatterns[index], newPatterns[index + 1]] = [
        newPatterns[index + 1],
        newPatterns[index],
      ];
    }
    setPatterns(newPatterns);
    updatePatternRanks(newPatterns);
  };

  return (
    <div>
      <div className="search-container-padding">
        <h3>Logdatei: {logFile.logFileName}</h3>
        <div className="search-container">
          <input
            className="form-control"
            type="text"
            placeholder="Patterns suchen..."
            value={searchTerm}
            onChange={handleSearchChange}
          />
          <button
            className="btn btn-primary"
            onClick={addPatternToLogFile}
            disabled={!selectedPattern}
          >
            Pattern hinzufügen
          </button>
          <button
            className="btn btn-secondary ml-2"
            onClick={openAddPatternModal}
            style={{ marginLeft: 10 }}
          >
            Neues Patterns erstellen
          </button>
        </div>
      </div>
      {isPatternDeleted && (
        <div
          className=" alert-margin-align alert alert-danger alert-dismissible fade show"
          role="alert"
        >
          <p>{message.message}</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setIsPatternDeleted(false)}
          ></button>
        </div>
      )}

      {isPatternAdded && (
        <div
          className=" alert-margin-align alert alert-success alert-dismissible fade show"
          role="alert"
        >
          <p>Neues Pattern wurde hinzugefügt</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setIsPatternAdded(false)}
          ></button>
        </div>
      )}

      {patternAdded.changed && isPatternAdded && (
        <div
          className=" alert-margin-align alert alert-success alert-dismissible fade show"
          role="alert"
        >
          <p>Neues Pattern wurde hinzugefügt</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setIsPatternAdded(false)}
          ></button>
        </div>
      )}

      {/* Dropdown for filtered patterns */}
      {searchTerm && (
        <ul className="pattern-dropdown">
          {filteredPatterns.map((pattern) => (
            <li
              className="pattern-dropdown-item"
              key={pattern.patternId}
              onClick={() => handlePatternSelect(pattern)}
            >
              {pattern.patternName} - {pattern.pattern} -{" "}
              {pattern.patternDescription} - {pattern.severity}
            </li>
          ))}
        </ul>
      )}

      {patterns.length === 0 ? (
        <p className="alert-margin-align">Keine Patterns gefunden.</p>
      ) : (
        <table className="log-files-table">
          <thead>
            <tr>
              <th>#</th>
              <th></th>
              <th>Pattern Name</th>
              <th>Pattern</th>
              <th>Beschreibung</th>
              <th>Schweregrad</th>
              <th>Läschen</th>
            </tr>
          </thead>
          <tbody>
            {patterns.map((pattern, index) => (
              <tr key={pattern.patternId}>
                <td>{pattern.rank}</td>
                <td>
                  <button
                    className="btn btn-sm btn-outline-secondary mr-1"
                    onClick={() => movePattern(index, "up")}
                    disabled={index === 0}
                  >
                    <FaArrowUp />
                  </button>
                  <button
                    className="btn btn-sm btn-outline-secondary"
                    onClick={() => movePattern(index, "down")}
                    disabled={index === patterns.length - 1}
                  >
                    <FaArrowDown />
                  </button>
                </td>
                <td>{pattern.patternName}</td>
                <td>{pattern.pattern}</td>
                <td>{pattern.patternDescription}</td>
                <td>{pattern.severity}</td>
                <td>
                  <button
                    className="btn btn-danger"
                    onClick={() => deletePattern(pattern.patternId)}
                  >
                    Löschen
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {isAddPatternModalOpen && (
        <AddPatternForm
          setPatternAdded={setPatternAdded}
          onClose={() => {
            setIsAddPatternModalOpen(false);
          }}
          logFileID={logFileID}
          newRank={newRank}
        />
      )}
    </div>
  );
};
