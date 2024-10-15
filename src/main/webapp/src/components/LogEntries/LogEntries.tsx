import React, { useEffect, useState } from "react";

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
    console.log(data);
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
    <div>
      {message && <div>{message}</div>}
      <table className="log-files-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Logeintrag</th>
            <th style={{ width: "10%" }}>Gefunden am</th>
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
                  <td>{logentry.schweregrad}</td>
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
