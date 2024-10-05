import React, { useEffect, useState } from "react";
import { ListLogFiles } from "./LogFiles/ListLogFiles";
import { AddLogFile } from "./LogFiles/AddLogFile";

export const MainPage = () => {
  return (
    <>
      <AddLogFile />
      <hr />
      <ListLogFiles />
    </>
  );
};
