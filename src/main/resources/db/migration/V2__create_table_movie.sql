CREATE TABLE movie(
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    genre VARCHAR(100) NOT NULL,
    rating DOUBLE PRECISION CHECK (rating >= 0 AND rating <= 10),
    release_date DATE NOT NULL,
    duration VARCHAR(20) NOT NULL, -- Ou VARCHAR(20) se preferir armazenar como string (ex: "2h10m")
    image BYTEA NOT NULL,
    trailer BYTEA NOT NULL,
    created_at TIMESTAMP NOT NULL ,
    updated_at TIMESTAMP NOT NULL
);