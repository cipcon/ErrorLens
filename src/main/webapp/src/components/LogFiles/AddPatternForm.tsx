import React, { useState } from "react";
import "./AddPatternForm.css";
import { MessageResponse, PatternLogFileRequest } from "./LogFile";

interface AddPatternFormProps {
  setPatternAdded: React.Dispatch<React.SetStateAction<AddPatternResponse>>;
  onClose: () => void;
  logFileID?: number;
  newRank?: number;
}

export interface AddPatternResponse {
  message: string;
  changed: boolean;
  patternId: number;
}

export interface PatternAddedToLogfile {
  message: string;
  changed: boolean;
}

export const AddPatternForm: React.FC<AddPatternFormProps> = ({
  setPatternAdded,
  onClose,
  logFileID,
  newRank,
}) => {
  const [patternName, setPatternName] = useState("");
  const [pattern, setPattern] = useState("");
  const [patternDescription, setPatternDescription] = useState("");
  const [severity, setSeverity] = useState("");
  const [responseMessage, setResponseMessage] = useState<AddPatternResponse>({
    message: "",
    changed: false,
    patternId: 0,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const addResponse = await fetch("/pattern/addPattern", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          patternName,
          pattern,
          patternDescription,
          severity,
        }),
      });
      const addData: AddPatternResponse = await addResponse.json();
      setResponseMessage(addData);
      if (addData.changed) {
        addData.changed && setPatternAdded(addData);
        addPatternToLogFile(addData.patternId);
        onClose();
      }
    } catch (error) {
      console.error("Error in form submission:", error);
      setResponseMessage({
        message: "An error occurred while processing your request",
        changed: false,
        patternId: 0,
      });
    }
  };

  const addPatternToLogFile = async (patternId: number) => {
    if (!logFileID) {
      return;
    }

    const addPatternRequest: PatternLogFileRequest = {
      logFileID: logFileID,
      patternID: patternId,
      rank: newRank || 0,
    };

    try {
      const response = await fetch("/logFilePattern/addPatternToLogFile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(addPatternRequest),
      });
      const data: PatternAddedToLogfile = await response.json();
      if (!data.changed) {
        console.error(data.message);
      }
    } catch (error) {
      console.error("Error adding pattern to log file:", error);
      throw error;
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Add New Pattern</h2>
        <form onSubmit={handleSubmit}>
          <input
            autoFocus
            type="text"
            placeholder="Pattern Name"
            value={patternName}
            onChange={(e) => setPatternName(e.target.value)}
            required
          />
          <input
            type="text"
            placeholder="Pattern"
            value={pattern}
            onChange={(e) => setPattern(e.target.value)}
            required
          />
          <textarea
            style={{ maxHeight: 200, minHeight: 100 }}
            placeholder="Pattern Beschreibung"
            value={patternDescription}
            onChange={(e) => setPatternDescription(e.target.value)}
            required
          />
          <select
            style={{ minWidth: 220 }}
            value={severity}
            onChange={(e) => setSeverity(e.target.value)}
            required
          >
            <option value="">Schweregrad auswählen</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="CRITICAL">CRITICAL</option>
          </select>
          <div className="button-group">
            <button type="submit" className="btn btn-primary">
              Pattern hinzufügen
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
            >
              Zurück
            </button>
          </div>
          {responseMessage.message && <p>{responseMessage.message}</p>}
        </form>
      </div>
    </div>
  );
};
