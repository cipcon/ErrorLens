import React from "react";

export const Message: React.FC = () => {
  const name = "Ciprian";

  return name ? <h1>Hello {name}</h1> : <h1>Hello World</h1>;
};
