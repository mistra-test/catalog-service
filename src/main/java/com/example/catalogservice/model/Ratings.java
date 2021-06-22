package com.example.catalogservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class Ratings implements Serializable {
    private List<Rating> ratingList;
}
