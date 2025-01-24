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
    paid BOOLEAN
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

INSERT INTO movie (name, description) values ('Люди в черном', 'Американский фильм про инопланетян');
INSERT INTO session (movie_id, price, datetime) values (1, 2000, '2025-01-01 10:00:00');

INSERT INTO place (name) values ('A12');

INSERT INTO ticket (place_id, session_id, paid) values (1, 1, false);

SELECT * from movie;
SELECT * from session;
SELECT * from place;
SELECT * from ticket;

DELETE FROM movie where movie.id > 1;