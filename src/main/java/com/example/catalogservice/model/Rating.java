package com.example.catalogservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating implements Serializable {

    private Long id;

    private Long userId;
    private Long movieId;
    private int score;
}
