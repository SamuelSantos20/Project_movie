CREATE TABLE movie (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    director TEXT NOT NULL,
    genre VARCHAR(100) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    description TEXT NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    image BYTEA NOT NULL,
    trailer BYTEA NOT NULL
);
