package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.UserRating;
import com.lorecodex.backend.repository.UserRatingRepository;
import com.lorecodex.backend.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    private final UserRatingRepository userRatingRepository;

    @Autowired
    public UserRatingServiceImpl(UserRatingRepository userRatingRepository) {
        this.userRatingRepository = userRatingRepository;
    }

    @Override
    public List<UserRating> getAllRatingsByGameId(Long gameId) {
        return userRatingRepository.findByGameId(gameId);
    }

    @Override
    public Optional<UserRating> getRatingByUserAndGame(Integer userId, Long gameId) {
        return userRatingRepository.findByUserIdAndGameId(userId, gameId);
    }

    @Override
    public UserRating saveOrUpdateRating(UserRating userRating) {
        return userRatingRepository.save(userRating);
    }

    @Override
    public void deleteRating(Long id) {
        userRatingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteRatingByUserAndGame(Integer userId, Long gameId) {
        userRatingRepository.deleteByUserIdAndGameId(userId, gameId);
    }

    @Override
    public Double calculateAverageRating(Long gameId) {
        List<UserRating> ratings = userRatingRepository.findByGameId(gameId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = ratings.stream()
                .mapToDouble(UserRating::getRating)
                .sum();

        return sum / ratings.size();
    }
}