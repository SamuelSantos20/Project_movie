CREATE TABLE history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    movie_id UUID NOT NULL,
    rating INT NOT NULL,
    view_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_history_movie FOREIGN KEY (movie_id) REFERENCES movie(id)
);
