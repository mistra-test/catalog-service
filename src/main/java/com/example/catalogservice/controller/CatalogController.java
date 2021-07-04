package com.example.catalogservice.controller;

import com.example.catalogservice.model.Movie;
import com.example.catalogservice.model.RatedMovie;
import com.example.catalogservice.model.Rating;
import com.example.catalogservice.service.CatalogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired private CatalogService catalogService;

    @GetMapping("/movies")
    public Flux<Movie> findAllMovies() {
        return catalogService.getMovieList();
    }

    @PreAuthorize("hasAuthority('movie:write')")
    @PostMapping("/movies")
    public Mono<Movie> save(@RequestBody Movie movie) {
        return catalogService.save(movie);
    }

    @GetMapping("/movies/rated")
    public Flux<RatedMovie> findAllRatedMovies(Principal principal) {
        var userId = Long.valueOf(principal.getName());
        return catalogService.findAllRatedMoviesByUserId(userId);
    }

    @PatchMapping("/movies/rate")
    public Mono<Rating> rateMovie(@RequestBody RatedMovie ratedMovie, Principal principal) {
        var userId = Long.valueOf(principal.getName());
        return catalogService.assignRating(userId, ratedMovie);
    }
}
