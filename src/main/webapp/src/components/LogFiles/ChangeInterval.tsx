import React, { useEffect, useState } from "react";

interface CheckIntervalProps {
  interval: number;
  timeUnit: "HOURS" | "MINUTES" | "SECONDS";
}

interface GetCheckIntervalProps {
  interval: number;
  timeUnit: string;
  lastCheck: string;
  timeUntilNextCheck: number;
}

export const ChangeInterval = () => {
  const [checkInterval, setCheckInterval] = useState<CheckIntervalProps>({
    interval: 0,
    timeUnit: "SECONDS",
  });
  const [checkIntervalResponse, setCheckIntervalResponse] =
    useState<string>("");
  const [ischanged, setIsChanged] = useState<boolean>(true);
  const [actualInterval, setActualInterval] = useState<GetCheckIntervalProps>({
    interval: 0,
    timeUnit: "",
    lastCheck: "",
    timeUntilNextCheck: 0,
  });

  useEffect(() => {
    getCheckInterval();
  }, [setActualInterval]);

  const sendCheckInterval = async () => {
    try {
      const response = await fetch("/fileChangeChecker/changeInterval", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(checkInterval),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.text();
      setCheckIntervalResponse(data);
      getCheckInterval();
    } catch (error) {
      console.error("Error sending check interval:", error);
    }
  };

  const getCheckInterval = async () => {
    try {
      const response = await fetch("/fileChangeChecker/getCheckInterval");
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data: GetCheckIntervalProps = await response.json();
      setActualInterval(data);
    } catch (error) {
      console.error("Error getting the check intervall:", error);
    }
  };

  const checkNow = async () => {
    try {
      const response = await fetch("/fileChangeChecker/checkNow");
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setCheckIntervalResponse(data.message);
    } catch (error) {
      console.error("Error checking now:", error);
    }
  };

  const setInterval = (interval: number) => {
    setCheckInterval({
      ...checkInterval,
      interval: interval,
    });
  };

  const setTimeUnit = (timeUnit: "HOURS" | "MINUTES" | "SECONDS") => {
    setCheckInterval({
      ...checkInterval,
      timeUnit: timeUnit,
    });
  };
  return (
    <>
      <h3 style={{ textAlign: "center" }}>Intervall der Prüfung auswählen</h3>

      <div className="change-check-interval-form">
        <label htmlFor="interval">Intervall</label>
        <input
          id="interval"
          type="number"
          value={checkInterval.interval}
          onChange={(e) => setInterval(parseInt(e.target.value))}
        />
        <label htmlFor="timeUnit">Zeiteinheit</label>
        <select
          id="timeUnit"
          value={checkInterval.timeUnit}
          onChange={(e) =>
            setTimeUnit(e.target.value as "HOURS" | "MINUTES" | "SECONDS")
          }
        >
          <option value="HOURS">Stunden</option>
          <option value="MINUTES">Minuten</option>
          <option value="SECONDS">Sekunden</option>
        </select>
        <button className="btn btn-primary" onClick={() => sendCheckInterval()}>
          Prüfintervall wählen
        </button>
        <button className="btn btn-primary" onClick={() => checkNow()}>
          Jetzt prüfen
        </button>
      </div>
      {checkIntervalResponse && ischanged && (
        <div
          className="alert-center-align alert alert-success alert-dismissible fade show"
          role="alert"
        >
          <p>{checkIntervalResponse}</p>
          <button
            type="button"
            className="btn-close"
            data-bs-dismiss="alert"
            aria-label="Close"
            onClick={() => setIsChanged(false)}
          ></button>
        </div>
      )}
      <p style={{ textAlign: "center" }}>
        Aktuelles Intervall {actualInterval.interval} {actualInterval.timeUnit}.
        Letze Überprüfung: {actualInterval.lastCheck}. Nächste Überprüfung:{" "}
        {(actualInterval.timeUntilNextCheck / 3600).toFixed(2)} Stunde(n)
      </p>
    </>
  );
};
