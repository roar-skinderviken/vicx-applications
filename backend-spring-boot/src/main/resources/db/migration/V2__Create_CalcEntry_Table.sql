CREATE TABLE calc_entry
(
    id           SERIAL PRIMARY KEY,
    first_value  INT        NOT NULL,
    second_value INT        NOT NULL,
    operation    VARCHAR(8) NOT NULL,
    result       INT        NOT NULL,
    username     VARCHAR(255) NULL,
    created_at   TIMESTAMP  NOT NULL
);
