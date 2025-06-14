
-- Association table for many-to-many relationship between Movie and Cast
CREATE TABLE movie_cast (
    movie_id UUID NOT NULL,
    cast_id UUID NOT NULL,
    PRIMARY KEY (movie_id, cast_id),
    FOREIGN KEY (movie_id) REFERENCES movie(id) ON DELETE CASCADE,
    FOREIGN KEY (cast_id) REFERENCES casts(id) ON DELETE CASCADE
);
