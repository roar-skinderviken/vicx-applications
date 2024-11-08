CREATE TABLE vicx_user
(
    id        SERIAL PRIMARY KEY,
    username  VARCHAR(255) NOT NULL,
    name      VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    image     VARCHAR(4000) NULL
);
