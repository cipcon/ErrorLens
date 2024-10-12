import React, { useState } from "react";
import "./AddPatternForm.css";
import { MessageResponse, PatternLogFileRequest } from "./LogFile";

interface AddPatternFormProps {
  onClose: () => void;
  onPatternAdded?: () => void;
  onAdded?: () => void;
  logFileID?: number;
  newRank?: number;
}

export const AddPatternForm: React.FC<AddPatternFormProps> = ({
  onClose,
  onPatternAdded,
  logFileID,
  newRank,
  onAdded,
}) => {
  const [patternName, setPatternName] = useState("");
  const [pattern, setPattern] = useState("");
  const [patternDescription, setPatternDescription] = useState("");
  const [severity, setSeverity] = useState("");
  const [responseMessage, setResponseMessage] = useState<MessageResponse>({
    message: "",
    changed: false,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await fetch("/pattern/addPattern", {
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
      const data: MessageResponse = await response.json();
      setResponseMessage(data);
      console.log(data);
      if (data.changed && onAdded) {
        onAdded();
      } else if (data.changed) {
        await getPatternID();
      }
    } catch (error) {
      console.error("Error adding new pattern:", error);
    }
  };

  const getPatternID = async () => {
    try {
      const response = await fetch("/pattern/getPatternID", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(patternName),
      });
      const data: number = await response.json();
      console.log("Pattern ID:", data);
      if (data != -1) {
        await addPatternToLogFile(data);
      }
    } catch (error) {
      console.error("Error getting pattern ID:", error);
      throw error;
    }
  };

  const addPatternToLogFile = async (patternID: number) => {
    const addPatternRequest: PatternLogFileRequest = {
      logFileID: logFileID || 0,
      patternID: patternID,
      rank: newRank || 0,
    };
    console.log("Adding pattern to log file:", addPatternRequest);
    try {
      const response = await fetch("/logFilePattern/addPatternToLogFile", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(addPatternRequest),
      });
      const data: MessageResponse = await response.json();
      if (data.changed) {
        onPatternAdded && onPatternAdded();
      }
      console.log("Pattern added to log file:", data);
    } catch (error) {
      console.error("Error adding pattern to log file:", error);
      throw error;
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Neues Pattern hinzufügen</h2>
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
            <button type="button" className="btn btn-primary" onClick={onClose}>
              Zurück
            </button>
          </div>
          {responseMessage && <p>{responseMessage.message}</p>}
        </form>
      </div>
    </div>
  );
};
