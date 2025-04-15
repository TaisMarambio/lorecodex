package com.lorecodex.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameResponse {
    private String name;
    private String summary;
    private List<Genre> genres;
    private List<Platform> platforms;
    private Cover cover;

    public static class Genre {
        public String name;
    }

    public static class Platform {
        public String name;
    }

    public static class Cover {
        @JsonProperty("url")
        public String imageUrl;
    }
}