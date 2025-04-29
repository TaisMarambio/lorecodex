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
    public Optional<UserRating> getRatingByUserAndGame(Long userId, Long gameId) {
        return userRatingRepository.findByUserIdAndGameId(userId, gameId);
    }

    @Override
    @Transactional
    public UserRating saveOrUpdateRating(UserRating userRating) {
        return userRatingRepository.save(userRating);
    }

    @Override
    @Transactional
    public void deleteRating(Long id) {
        userRatingRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteRatingByUserAndGame(Long userId, Long gameId) {
        userRatingRepository.deleteByUserIdAndGameId(userId, gameId);
    }

    @Override
    public Double calculateAverageRating(Long gameId) {
        try {
            // First attempt to use the repository query method
            Double average = userRatingRepository.calculateAverageRatingForGame(gameId);

            // If the query returned null (no ratings), return 0.0
            if (average == null) {
                return 0.0;
            }

            // Round to 1 decimal place for better presentation
            return Math.round(average * 10.0) / 10.0;
        } catch (Exception e) {
            // Fallback calculation method if the query fails
            List<UserRating> ratings = userRatingRepository.findByGameId(gameId);

            if (ratings == null || ratings.isEmpty()) {
                return 0.0;
            }

            double sum = 0.0;
            int count = 0;

            for (UserRating rating : ratings) {
                if (rating.getRating() != null) {
                    sum += rating.getRating();
                    count++;
                }
            }

            if (count == 0) {
                return 0.0;
            }

            // Round to 1 decimal place
            return Math.round((sum / count) * 10.0) / 10.0;
        }
    }
}