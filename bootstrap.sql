DROP TABLE IF EXISTS observation;
DROP TABLE IF EXISTS celestial_body;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    avatar TEXT,
    biography TEXT,
    is_public BOOLEAN NOT NULL,
    type SMALLINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE celestial_body (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    image TEXT NOT NULL,
    validity_time INTEGER NOT NULL
);

CREATE TABLE observation (
    id SERIAL PRIMARY KEY,
    description TEXT,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL,
    orientation INT NOT NULL,
    celestial_body_id SERIAL NOT NULL,
    author_id SERIAL NOT NULL,
    visibility SMALLINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_observation_celestial_body FOREIGN KEY (celestial_body_id) REFERENCES celestial_body(id) ON DELETE CASCADE,
    CONSTRAINT fk_observation_user FOREIGN KEY (author_id) REFERENCES "user"(id) ON DELETE CASCADE
);
