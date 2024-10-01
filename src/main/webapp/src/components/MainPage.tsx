import React from "react";
import { ListLogFiles } from "./LogFiles/ListLogFiles";
import { AddLogFile } from "./LogFiles/AddLogFile";

export const MainPage = () => {
  return (
    <div>
      <AddLogFile />
      <hr />
      <ListLogFiles />
    </div>
  );
};
