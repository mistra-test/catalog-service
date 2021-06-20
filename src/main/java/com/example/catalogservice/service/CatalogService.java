package com.example.catalogservice.service;

import com.example.catalogservice.exception.NullWrapperException;
import com.example.catalogservice.exception.UnauthorizedOperationException;
import com.example.catalogservice.model.Movie;
import com.example.catalogservice.model.RatedMovie;
import com.example.catalogservice.model.Rating;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
class Movies implements Serializable {
    private List<Movie> movieList;
}

@Data
class Ratings implements Serializable {
    private List<Rating> ratingList;
}

@Service
public class CatalogService {
    private RestTemplate restTemplate;
    //  private JmsTemplate jmsTemplate;

    @Autowired
    public CatalogService(RestTemplate restTemplate /*, JmsTemplate jmsTemplate*/) {
        this.restTemplate = restTemplate;
        // this.jmsTemplate = jmsTemplate;
    }

    public List<Movie> getMovieList() {

        Movies movieList = restTemplate.getForObject("http://movie-resource/movies", Movies.class);

        if (movieList == null) throw new NullWrapperException();

        return movieList.getMovieList();
    }

    public List<RatedMovie> findAllRatedMoviesByUserId(Long userId) {

        Ratings ratingList =
                restTemplate.getForObject(
                        "http://rating-resource/ratings/findByUser/" + userId, Ratings.class);
        if (ratingList == null) throw new NullWrapperException();

        return ratingList.getRatingList().stream()
                .map(this::joinRatingWithMovie)
                .collect(Collectors.toList());
    }

    @Transactional
    public Movie save(Movie movie) {
        if (!contextUserCanWriteMovies())
            throw new UnauthorizedOperationException("movie saving grant is missing");
        var savedMovie =
                restTemplate.postForObject("http://movie-resource/movies", movie, Movie.class);
        // jmsTemplate.convertAndSend("test", "pluto is not a planet");
        return savedMovie;
    }

    public Rating assignRating(Long userId, RatedMovie ratedMovie) {
        var rating = new Rating();
        rating.setScore(ratedMovie.getScore());
        rating.setMovieId(ratedMovie.getMovieId());
        rating.setUserId(userId);
        return restTemplate.postForObject("http://rating-resource/ratings", rating, Rating.class);
    }

    private RatedMovie joinRatingWithMovie(Rating rating) {
        var movie =
                restTemplate.getForObject(
                        "http://movie-resource/movies/" + rating.getMovieId(), Movie.class);
        return RatedMovie.from(movie, rating);
    }

    private boolean contextUserCanWriteMovies() {
        List<String> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .map(GrantedAuthority::toString)
                        .collect(Collectors.toList());

        return authorities.contains("movie_write");
    }
}
