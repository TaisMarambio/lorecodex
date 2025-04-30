package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.UserRatingRequest;
import com.lorecodex.backend.dto.response.UserRatingResponse;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import org.springframework.stereotype.Component;

@Component
public class UserRatingMapper { //convertimos entre entidad y DTO

    public UserRating toEntity(UserRatingRequest dto, User user, Game game) {
        UserRating rating = new UserRating();
        rating.setUser(user);
        rating.setGame(game);
        rating.setRating(dto.getRating());
        return rating;
    }

    public UserRatingResponse toDTO(UserRating rating) {
        return new UserRatingResponse(
                rating.getId(),
                rating.getGame().getId(),
                rating.getUser().getId(),
                rating.getRating()
        );
    }
}
