package com.example.catalogservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class Movies implements Serializable {
    private List<Movie> movieList;
}
