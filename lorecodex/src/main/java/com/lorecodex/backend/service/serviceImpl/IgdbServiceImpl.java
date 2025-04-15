package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.GameResponse;
import com.lorecodex.backend.service.IgdbService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class IgdbServiceImpl implements IgdbService {

    @Value("${igdb.client-id}")
    private String clientId;

    @Value("${igdb.client-secret}")
    private String clientSecret;

    private String accessToken;

    @Override
    public String getAccessToken() {
        if (accessToken != null) return accessToken;

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://id.twitch.tv/oauth2/token" +
                "?client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=client_credentials";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
        accessToken = (String) response.getBody().get("access_token");

        return accessToken;
    }

    @Override
    public List<GameResponse> searchGames(String query) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-ID", clientId);
        headers.set("Authorization", "Bearer " + getAccessToken());
        headers.setContentType(MediaType.TEXT_PLAIN);

        String body = "fields name,summary,genres.name,platforms.name,cover.url; search \"" + query + "\"; limit 5;";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<GameResponse[]> response = restTemplate.postForEntity(
                "https://api.igdb.com/v4/games",
                request,
                GameResponse[].class
        );

        return Arrays.asList(response.getBody());
    }
}
