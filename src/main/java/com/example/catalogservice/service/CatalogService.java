package com.example.catalogservice.service;

import com.example.catalogservice.config.ResourcesLocation;
import com.example.catalogservice.model.Movie;
import com.example.catalogservice.model.RatedMovie;
import com.example.catalogservice.model.Rating;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final WebClient.Builder webClientBuilder;

    private final ResourcesLocation resourcesLocation;

    public Flux<Movie> getMovieList() {
        return webClientBuilder
                .build()
                .get()
                .uri(resourcesLocation.getMovieResource() + "/movies")
                .retrieve()
                .bodyToFlux(Movie.class);
    }

    public Flux<RatedMovie> findAllRatedMoviesByUserId(Long userId) {
        return webClientBuilder
                .build()
                .get()
                .uri(resourcesLocation.getRatingResource() + "/ratings/findByUser/" + userId)
                .retrieve()
                .bodyToFlux(Rating.class)
                .flatMap(this::joinRatingWithMovie);
    }

    public Mono<Movie> save(Movie movie) {
        return webClientBuilder
                .build()
                .post()
                .uri(resourcesLocation.getMovieResource() + "/movies")
                .bodyValue(movie)
                .retrieve()
                .bodyToMono(Movie.class);
    }

    public Mono<Rating> assignRating(Long userId, RatedMovie ratedMovie) {

        var rating = new Rating();
        rating.setScore(ratedMovie.getScore());
        rating.setMovieId(ratedMovie.getMovieId());
        rating.setUserId(userId);

        return webClientBuilder
                .build()
                .post()
                .uri(resourcesLocation.getRatingResource() + "/ratings")
                .bodyValue(rating)
                .retrieve()
                .bodyToMono(Rating.class);
    }

    private Mono<RatedMovie> joinRatingWithMovie(Rating rating) {
        return webClientBuilder
                .build()
                .get()
                .uri(resourcesLocation.getMovieResource() + "/movies/" + rating.getMovieId())
                .retrieve()
                .bodyToMono(Movie.class)
                .map(movie -> RatedMovie.from(movie, rating));
    }
}
