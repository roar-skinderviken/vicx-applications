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
    id           SERIAL PRIMARY KEY,
    user_id      INT         NOT NULL,
    content_type VARCHAR(16) NOT NULL,
    image_data   BYTEA       NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES vicx_user (id)
);
