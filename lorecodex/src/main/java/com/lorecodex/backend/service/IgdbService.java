package com.lorecodex.backend.service;

import com.lorecodex.backend.dto.response.GameResponse;

import java.util.List;

public interface IgdbService {
    String getAccessToken();
    List<GameResponse> searchGames(String query);

}
