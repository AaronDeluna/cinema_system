CREATE TABLE place
(
    id   SERIAL PRIMARY KEY,
    name varchar(50)
);

CREATE TABLE ticket
(
    id         SERIAL PRIMARY KEY,
    place_id   INT REFERENCES place (id),
    session_id INT REFERENCES session (id),
    is_payment BOOLEAN
);

CREATE TABLE session
(
    id       SERIAL PRIMARY KEY,
    movie_id INT REFERENCES movie (id),
    price    NUMERIC(10, 2),
    datetime TIMESTAMP
);

CREATE TABLE movie
(
    id          SERIAL PRIMARY KEY,
    name        varchar(50),
    description TEXT
);