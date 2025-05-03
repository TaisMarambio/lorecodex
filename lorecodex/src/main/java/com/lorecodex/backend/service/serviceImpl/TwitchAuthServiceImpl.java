package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.service.TwitchAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TwitchAuthServiceImpl implements TwitchAuthService {

    @Value("${igdb.client-id}")
    private String clientId;

    @Value("${igdb.client-secret}")
    private String clientSecret;

    private String accessToken;
    private long tokenExpirationTime;

    @Override
    public String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() > tokenExpirationTime) {
            WebClient webClient = WebClient.create("https://id.twitch.tv/oauth2/token");

            Map<String, String> params = new HashMap<>();
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("grant_type", "client_credentials");

            Map response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("client_id", clientId)
                            .queryParam("client_secret", clientSecret)
                            .queryParam("grant_type", "client_credentials")
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            accessToken = (String) response.get("access_token");
            Integer expiresIn = (Integer) response.get("expires_in");
            tokenExpirationTime = System.currentTimeMillis() + expiresIn * 1000L;
        }
        return accessToken;
    }
}