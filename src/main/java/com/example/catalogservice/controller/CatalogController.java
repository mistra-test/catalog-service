package com.example.catalogservice.controller;

import com.example.catalogservice.model.Movie;
import com.example.catalogservice.model.RatedMovie;
import com.example.catalogservice.model.Rating;
import com.example.catalogservice.service.CatalogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired private CatalogService catalogService;

    @GetMapping("/movies")
    public List<Movie> findAllMovies() {
        return catalogService.getMovieList();
    }

    @PostMapping("/movies")
    public Movie save(@RequestBody Movie movie) {
        return catalogService.save(movie);
    }

    @GetMapping("/movies/rated")
    public List<RatedMovie> findAllRatedMovies(Principal principal) {
        var userId = Long.valueOf(principal.getName());
        return catalogService.findAllRatedMoviesByUserId(userId);
    }

    @PatchMapping("/movies/rate")
    public Rating rateMovie(@RequestBody RatedMovie ratedMovie, Principal principal) {
        var userId = Long.valueOf(principal.getName());
        return catalogService.assignRating(userId, ratedMovie);
    }
}
