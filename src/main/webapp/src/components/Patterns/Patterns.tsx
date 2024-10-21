import React, { useEffect, useMemo, useState } from "react";
import {
  AddPatternResponse,
  MessageResponse,
  Pattern,
} from "../LogFiles/LogFile";
import { AddPatternForm } from "../LogFiles/AddPatternForm";
import "../LogFiles/AddPatternForm.css";
import "./Patterns.css";

export const Patterns: React.FC = () => {
  const [patterns, setPatterns] = useState<Pattern[]>([]);
  const [message, setMessage] = useState<MessageResponse>({
    message: "",
    changed: false,
  });
  const [isAddPatternModalOpen, setIsAddPatternModalOpen] = useState(false);
  const [patternAdded, setPatternAdded] = useState<AddPatternResponse>({
    message: "",
    changed: false,
    patternId: 0,
  });
  const [searchTerm, setSearchTerm] = useState("");
  const [added, setAdded] = useState<boolean>(true);
  const [deleted, setDeleted] = useState<boolean>(true);

  useEffect(() => {
    listPatterns();
  }, [patternAdded]);

  const listPatterns = async () => {
    try {
      const response = await fetch("/pattern/listPatterns");
      const data = await response.json();
      setPatterns(data);
    } catch (error) {
      console.error("Error listing patterns:", error);
    }
  };

  const deletePattern = async (patternId: number) => {
    if (
      !window.confirm(
        "Sind Sie sicher, dass Sie das Pattern löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden."
      )
    ) {
      return;
    }

    try {
      const response = await fetch("/pattern/deletePattern", {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(patternId),
      });
      const data = await response.json();
      setMessage(data);
      if (data.changed) {
        setDeleted(true);
        listPatterns();
      }
    } catch (error) {
      console.error("Error deleting pattern:", error);
    }
  };

  const filteredPatterns = useMemo(
    () =>
      patterns.filter((pattern) =>
        pattern.patternName.toLowerCase().includes(searchTerm.toLowerCase())
      ),
    [patterns, searchTerm]
  );

  return (
    <>
      <div className="new-and-search">
        <button
          className="btn btn-primary"
          onClick={() => setIsAddPatternModalOpen(true)}
        >
          Add New Pattern
        </button>
        <input
          className="form-control"
          type="text"
          placeholder="Search patterns..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>
      {isAddPatternModalOpen && (
        <AddPatternForm
          setPatternAdded={setPatternAdded}
          onClose={() => setIsAddPatternModalOpen(false)}
        />
      )}
      {deleted && message.changed && (
        <div
          className=" alert-margin-left-top-align alert alert-danger alert-dismissible fade show"
          role="alert"
        >
          <p>{message.message}</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setDeleted(false)}
          ></button>
        </div>
      )}
      {added && patternAdded.changed && (
        <div
          className=" alert-margin-left-top-align alert alert-success alert-dismissible fade show"
          role="alert"
        >
          <p>Neues Pattern wurde hinzugefügt</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setAdded(false)}
          ></button>
        </div>
      )}
      {filteredPatterns.length === 0 ? (
        <p className="pattern-message-style">Keine Patterns gefunden</p>
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
            {filteredPatterns.map((pattern, index) => (
              <tr key={pattern.patternId}>
                <td>{index + 1}</td>
                <td>{pattern.patternName}</td>
                <td>{pattern.pattern}</td>
                <td>{pattern.patternDescription}</td>
                <td
                  style={{
                    color:
                      pattern.severity === "CRITICAL"
                        ? "#cc0000"
                        : pattern.severity === "HIGH"
                        ? "#cc7a00"
                        : pattern.severity === "MEDIUM"
                        ? "#8B8000"
                        : "inherit", // Default color if none match
                    textDecoration: "bold",
                  }}
                >
                  {pattern.severity}
                </td>
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
    </>
  );
};
