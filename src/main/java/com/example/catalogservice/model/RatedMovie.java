package com.example.catalogservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatedMovie implements Serializable {
    private Long movieId;
    private String movieName;
    private String movieDescription;
    private Integer score;

    public static RatedMovie from(Movie movie, Rating rating) {
        var ratedMovie = new RatedMovie();
        ratedMovie.setMovieId(movie.getId());
        ratedMovie.setMovieName(movie.getName());
        ratedMovie.setMovieDescription(movie.getDescription());
        ratedMovie.setScore(rating.getScore());
        return ratedMovie;
    }
}
