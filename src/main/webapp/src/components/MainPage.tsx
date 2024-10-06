import React, { useEffect, useState } from "react";
import { ListLogFiles } from "./LogFiles/ListLogFiles";
import { AddLogFile } from "./LogFiles/AddLogFile";
import { CheckInterval } from "./LogFiles/CheckInterval";

export const MainPage = () => {
  return (
    <>
      <AddLogFile />
      <hr />
      <CheckInterval />
      <hr />
      <ListLogFiles />
    </>
  );
};
