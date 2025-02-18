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
    paid       BOOLEAN
);

select *
from ticket t join place p on p.id = t.place_id
where session_id = 1 and p.name = 'A3';


select * from ticket;
select * from session;