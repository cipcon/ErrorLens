import React, { useEffect, useState } from "react";
import "./LogEntries.css";

interface LogentriesResponse {
  logeintrag_id: number;
  logeintrag_beschreibung: string;
  gefunden_am: string;
  logfile_name: string;
  pattern_name: string;
  schweregrad: string;
}

export const LogEntries = () => {
  const [logentries, setLogentries] = useState<LogentriesResponse[]>([]);
  const [message, setMessage] = useState<string>("");

  const fetchLogentries = async () => {
    const response = await fetch("/logEntries/getLogentries");
    const data = await response.json();
    setLogentries(data);
  };

  const deleteLogEntry = async (logEntryId: number) => {
    const isConfirmed = window.confirm(
      `Sind Sie sicher, dass Sie den Logeintrag löschen möchten? Diese Aktion kann nicht rückgängig gemacht werden.`
    );

    if (!isConfirmed) return;
    const response = await fetch("/logEntries/deleteLogEntry", {
      method: "POST",
      body: JSON.stringify(logEntryId),
    });
    const data = await response.json();
    setMessage(data.message);
    fetchLogentries();
  };

  useEffect(() => {
    fetchLogentries();
  }, []);

  return (
    <div className="table-container">
      {message && <div>{message}</div>}
      <table className="logentries-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Logeintrag</th>
            <th>Gefunden am</th>
            <th>Logfile</th>
            <th>Pattern</th>
            <th>Schweregrad</th>
            <th>Löschen</th>
          </tr>
        </thead>
        <tbody>
          {logentries ? (
            <>
              {logentries.map((logentry, index) => (
                <tr key={logentry.logeintrag_id}>
                  <td>{index + 1}</td>
                  <td>{logentry.logeintrag_beschreibung}</td>
                  <td>{logentry.gefunden_am}</td>
                  <td>{logentry.logfile_name}</td>
                  <td>{logentry.pattern_name}</td>
                  <td
                    style={{
                      color:
                        logentry.schweregrad === "CRITICAL"
                          ? "#cc0000"
                          : logentry.schweregrad === "HIGH"
                          ? "#cc7a00"
                          : logentry.schweregrad === "MEDIUM"
                          ? "#8B8000"
                          : "inherit", // Default color if none match
                    }}
                  >
                    {logentry.schweregrad}
                  </td>
                  <td>
                    <button
                      className="btn btn-danger"
                      onClick={() => deleteLogEntry(logentry.logeintrag_id)}
                    >
                      Löschen
                    </button>
                  </td>
                </tr>
              ))}
            </>
          ) : (
            <tr>
              <td colSpan={4}>Keine Logeinträge gefunden</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};
