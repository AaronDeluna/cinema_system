CREATE TABLE movie
(
    id          SERIAL PRIMARY KEY,
    name        varchar(50),
    description TEXT
);

CREATE TABLE session
(
    id       SERIAL PRIMARY KEY,
    movie_id INT REFERENCES movie (id),
    price    NUMERIC(10, 2),
    datetime TIMESTAMP
);

CREATE TABLE place
(
    id   SERIAL PRIMARY KEY,
    name varchar(10)
);

CREATE TABLE ticket
(
    id         SERIAL PRIMARY KEY,
    place_id   INT REFERENCES place (id),
    session_id INT REFERENCES session (id),
    paid BOOLEAN
);

SELECT * from movie;
SELECT * from session;
SELECT * from place;
SELECT * from ticket;

-- DELETE FROM session;
-- DELETE FROM place;
-- DELETE FROM ticket;

-- DROP table ticket;
-- DROP table movie;
-- DROP table session;
-- DROP table place;

SELECT *
from ticket join session s on s.id = ticket.session_id
where s.id = 2;

UPDATE ticket SET paid = true
where id = 10
