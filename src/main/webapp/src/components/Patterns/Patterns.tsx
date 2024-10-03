import React, { useEffect, useState } from "react";
import { MessageResponse, Pattern } from "../LogFiles/LogFile";
import { AddPatternForm } from "../LogFiles/AddPatternForm";
import "../LogFiles/AddPatternForm.css";

export const Patterns = () => {
  const [patterns, setPatterns] = useState<Pattern[]>([]);
  const [Message, setMessage] = useState<MessageResponse>({
    message: "",
    changed: false,
  });
  const [isAddPatternModalOpen, setIsAddPatternModalOpen] = useState(false);

  useEffect(() => {
    listPatterns();
  }, []);

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
    const isConfirmed = window.confirm(
      `Are you sure you want to delete the pattern? This action cannot be undone.`
    );
    console.log("Deleting pattern:", patternId);

    if (!isConfirmed) return;

    try {
      const response = await fetch("/pattern/deletePattern", {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(patternId),
      });
      const data = await response.json();
      if (data.changed) {
        setMessage(data);
        listPatterns();
      } else {
        setMessage(data);
      }
    } catch (error) {
      console.error("Error deleting pattern:", error);
    }
  };

  const openAddPatternModal = () => {
    setIsAddPatternModalOpen(true);
  };

  const handlePatternAdded = () => {
    setIsAddPatternModalOpen(false);
    listPatterns();
  };

  return (
    <div>
      <h3>Patterns</h3>
      <button className="btn btn-primary" onClick={openAddPatternModal}>
        Add New Pattern
      </button>
      {isAddPatternModalOpen && (
        <AddPatternForm
          onClose={() => setIsAddPatternModalOpen(false)}
          onPatternAdded={handlePatternAdded}
        />
      )}
      {Message.message && <p>{Message.message}</p>}
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
            {patterns.map((pattern, index) => (
              <tr key={pattern.patternId}>
                <td>{index + 1}</td>
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
    </div>
  );
};
