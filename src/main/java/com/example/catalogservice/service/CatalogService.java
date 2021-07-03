package com.example.catalogservice.service;

import com.example.catalogservice.config.ResourcesLocation;
import com.example.catalogservice.exception.NullWrapperException;
import com.example.catalogservice.model.Movie;
import com.example.catalogservice.model.Movies;
import com.example.catalogservice.model.RatedMovie;
import com.example.catalogservice.model.Rating;
import com.example.catalogservice.model.Ratings;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final RestTemplate restTemplate;

    private final ResourcesLocation resourcesLocation;

    public List<Movie> getMovieList() {
        var url = resourcesLocation.getMovieResource() + "/movies";
        var movieList = restTemplate.getForObject(url, Movies.class);

        if (movieList == null) throw new NullWrapperException();

        return movieList.getMovieList();
    }

    public List<RatedMovie> findAllRatedMoviesByUserId(Long userId) {
        log.debug("fetching all the rated movies for user {}", userId);

        var url = resourcesLocation.getRatingResource() + "/ratings/findByUser/" + userId;
        var ratingList = restTemplate.getForObject(url, Ratings.class);

        if (ratingList == null) throw new NullWrapperException();

        return ratingList.getRatingList().stream()
                .map(this::joinRatingWithMovie)
                .collect(Collectors.toList());
    }

    @Secured("ROLE_movie:write")
    public Movie save(Movie movie) {
        var url = resourcesLocation.getMovieResource() + "/movies";

        return restTemplate.postForObject(url, movie, Movie.class);
    }

    public Rating assignRating(Long userId, RatedMovie ratedMovie) {
        var rating = new Rating();
        rating.setScore(ratedMovie.getScore());
        rating.setMovieId(ratedMovie.getMovieId());
        rating.setUserId(userId);

        var url = resourcesLocation.getRatingResource() + "/ratings";

        return restTemplate.postForObject(url, rating, Rating.class);
    }

    private RatedMovie joinRatingWithMovie(Rating rating) {

        var url = resourcesLocation.getMovieResource() + "/movies/" + rating.getMovieId();
        var movie = restTemplate.getForObject(url, Movie.class);

        return RatedMovie.from(movie, rating);
    }
}
