import React, { useState } from "react";

interface CheckIntervalProps {
  interval: number;
  timeUnit: "HOURS" | "MINUTES" | "SECONDS";
}

export const CheckInterval = () => {
  const [checkInterval, setCheckInterval] = useState<CheckIntervalProps>({
    interval: 0,
    timeUnit: "SECONDS",
  });
  const [checkIntervalResponse, setCheckIntervalResponse] =
    useState<string>("");

  const sendCheckInterval = async () => {
    console.log(checkInterval);
    try {
      const response = await fetch("/fileChangeChecker/checkInterval", {
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
    } catch (error) {
      console.error("Error sending check interval:", error);
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
      console.log(data);
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
          <option value="HOURS">Hours</option>
          <option value="MINUTES">Minutes</option>
          <option value="SECONDS">Seconds</option>
        </select>
        <button className="btn btn-primary" onClick={() => sendCheckInterval()}>
          Prüfintervall wählen
        </button>
        <button className="btn btn-primary" onClick={() => checkNow()}>
          Jetzt prüfen
        </button>
      </div>
      {checkIntervalResponse && (
        <p style={{ textAlign: "center" }}>{checkIntervalResponse}</p>
      )}
    </>
  );
};
