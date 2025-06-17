package com.lorecodex.backend.mapper;

import com.lorecodex.backend.dto.request.RatingRequest;
import com.lorecodex.backend.dto.response.UserRatingResponse;
import com.lorecodex.backend.model.Game;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.model.UserRating;
import org.springframework.stereotype.Component;

@Component
public class UserRatingMapper {

    // Crea entidad a partir de RatingRequest, usuario y juego
    public UserRating toEntity(RatingRequest dto, User user, Game game) {
        UserRating rating = new UserRating();
        rating.setUser(user);
        rating.setGame(game);
        rating.setRating(dto.getRating());
        return rating;
    }

    // Mapea correctamente userId y gameId
    public UserRatingResponse toDTO(UserRating rating) {
        return UserRatingResponse.builder()
                .id(rating.getId())
                .userId(rating.getUser().getId())
                .gameId(rating.getGame().getId())
                .rating(rating.getRating())
                .build();
    }
}
