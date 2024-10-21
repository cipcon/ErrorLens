import React, { useState } from "react";
import { ListLogFiles } from "./LogFiles/ListLogFiles";
import { AddLogFile } from "./LogFiles/AddLogFile";
import { ChangeInterval } from "./LogFiles/ChangeInterval";

export const MainPage = () => {
  const [logfileAdded, setLogfileAdded] = useState<boolean>(false);
  return (
    <>
      <AddLogFile setLogfileAdded={setLogfileAdded} />
      <hr />
      <ChangeInterval />
      <hr />
      <ListLogFiles logfileAdded={logfileAdded} />
    </>
  );
};
