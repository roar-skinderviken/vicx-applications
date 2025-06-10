CREATE TABLE vicx_user
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX unique_username_ci ON vicx_user (LOWER(username));

CREATE TABLE user_image
(
    user_id      INT PRIMARY KEY,
    content_type VARCHAR(16) NOT NULL,
    image_data   BYTEA       NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES vicx_user (id)
);

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
CREATE INDEX idx_calc_entry_id_desc ON calc_entry (id DESC);
CREATE INDEX idx_username ON calc_entry (username);
