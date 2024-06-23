DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id_user SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
    id_request SERIAL PRIMARY KEY,
    description VARCHAR,
    creation_date TIMESTAMP NOT NULL,
    id_user INTEGER REFERENCES users (id_user)
);

CREATE TABLE IF NOT EXISTS items (
    id_item SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR NOT NULL,
    available BOOLEAN NOT NULL,
    id_user INTEGER REFERENCES users (id_user),
    id_request INTEGER REFERENCES requests (id_request)
);

CREATE TABLE IF NOT EXISTS bookings (
    id_booking SERIAL PRIMARY KEY,
    start_booking TIMESTAMP NOT NULL,
    end_booking TIMESTAMP NOT NULL,
    id_item INTEGER REFERENCES items (id_item),
    id_user INTEGER REFERENCES users (id_user),
    status VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS comments (
    id_comment SERIAL PRIMARY KEY,
    text VARCHAR NOT NULL,
    id_item INTEGER REFERENCES items (id_item),
    id_author INTEGER REFERENCES users (id_user),
    creation_date TIMESTAMP NOT NULL
);
