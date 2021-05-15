package com.example.catalogservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie implements Serializable {

    private Long id;

    private String name;
    private String description;

    public static Movie from(Long id, String name, String description) {
        Movie movie = new Movie();
        movie.id = id;
        movie.name = name;
        movie.description = description;
        return movie;
    }
}
