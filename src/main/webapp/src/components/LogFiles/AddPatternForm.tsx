import React, { useState } from "react";
import "./AddPatternForm.css";
import { MessageResponse, PatternLogFileRequest } from "./LogFile";

interface AddPatternFormProps {
  onClose: () => void;
  onPatternAdded: () => void;
  logFileID?: number;
  newRank?: number;
}

export const AddPatternForm: React.FC<AddPatternFormProps> = ({
  onClose,
  onPatternAdded,
  logFileID,
  newRank,
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
      const data = await response.json();
      setResponseMessage(data.message);
      console.log(data);
      if (data.changed) {
        await handlePatternAdded();
      }
    } catch (error) {
      console.error("Error adding new pattern:", error);
    }
  };

  const handlePatternAdded = async () => {
    if (logFileID == null || newRank == null) {
      onPatternAdded();
      return;
    }
    try {
      const patternID = await getPatternID();
      await addPatternToLogFile(patternID);
      onPatternAdded();
    } catch (error) {
      console.error("Error in handlePatternAdded:", error);
    }
  };

  const getPatternID = async (): Promise<number> => {
    try {
      const response = await fetch("/pattern/getPatternID", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(patternName),
      });
      const data = await response.json();
      console.log("Pattern Name:", patternName);
      console.log("Pattern ID:", data);
      return data;
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
      const data = await response.json();
      console.log("Pattern added to log file:", data);
      setResponseMessage(data.message);
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
            placeholder="Pattern Description"
            value={patternDescription}
            onChange={(e) => setPatternDescription(e.target.value)}
            required
          />
          <select
            value={severity}
            onChange={(e) => setSeverity(e.target.value)}
            required
          >
            <option value="">Select Severity</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="CRITICAL">CRITICAL</option>
          </select>
          <div className="button-group">
            <button type="submit">Add Pattern</button>
            <button type="button" onClick={onClose}>
              Cancel
            </button>
          </div>
          {responseMessage.changed && <p>{responseMessage.message}</p>}
        </form>
      </div>
    </div>
  );
};
