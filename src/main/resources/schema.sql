CREATE TABLE IF NOT EXISTS CURRENT_EPISODE
(
  ID      INT PRIMARY KEY IDENTITY,
  NAME    VARCHAR(200),
  EPISODE VARCHAR(4),
  DATE    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS SERIAL
(
  ID           INT PRIMARY KEY IDENTITY,
  NAME         VARCHAR(200),
  LAST_UPDATED TIMESTAMP
);
