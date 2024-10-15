import React, { useState } from "react";
import { ListLogFiles } from "./LogFiles/ListLogFiles";
import { AddLogFile } from "./LogFiles/AddLogFile";
import { CheckInterval } from "./LogFiles/CheckInterval";

export const MainPage = () => {
  const [logfileAdded, setLogfileAdded] = useState<boolean>(false);
  return (
    <>
      <AddLogFile
        logfileAdded={logfileAdded}
        setLogfileAdded={setLogfileAdded}
      />
      <hr />
      <CheckInterval />
      <hr />
      <ListLogFiles logfileAdded={logfileAdded} />
    </>
  );
};
