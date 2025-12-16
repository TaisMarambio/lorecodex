package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.UserProfileResponse;
import com.lorecodex.backend.exception.UsernameConflictException;
import com.lorecodex.backend.mapper.ReviewMapper;
import com.lorecodex.backend.mapper.UserProfileMapper;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FollowService followService;
    private final GuideService guideService;
    private final UserListService userListService;
    private final ReviewMapper reviewMapper;
    private final UserProfileMapper userProfileMapper;
    private final ReviewService reviewService;

    public UserServiceImpl(UserRepository userRepository, FollowService followService, GuideService guideService, UserListService userListService, ReviewService reviewService, ReviewMapper reviewMapper, UserProfileMapper userProfileMapper) {
        this.userRepository = userRepository;
        this.followService = followService;
        this.guideService = guideService;
        this.userListService = userListService;
        this.reviewMapper = reviewMapper;
        this.reviewService = reviewService;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserProfileResponse getUserProfileById(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isFollowing = (currentUserId != null) && followService.isFollowing(currentUserId, user.getId());
        int followersCount = followService.getFollowers(user.getId()).size();
        int followingCount = followService.getFollowing(user.getId()).size();

        var guides = guideService.getPublishedGuidesByUserId(user.getId());
        var lists = userListService.getListsForUser(user.getId());
        var reviews = reviewMapper.toDTOList(reviewService.getReviewsByUserId(user.getId()));

        return userProfileMapper.toProfileResponse(
                user,
                isFollowing,
                followersCount,
                followingCount,
                guides,
                lists,
                reviews
        );
    }

    @Override
    public Long findUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(null);
    }

    @Override
    public UserProfileResponse getUserProfileByUsername(String username, Long currentUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return getUserProfileById(user.getId(), currentUserId);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserDetails loadUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private static final int USERNAME_MIN = 3;
    private static final int USERNAME_MAX = 15;
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9._-]+$";

    @Override
    @Transactional
    public User updateUsername(Long userId, String newUsername) {
        String candidate = newUsername == null ? "" : newUsername.trim();
        if (candidate.isEmpty()) {
            throw new IllegalArgumentException("Username must not be empty");
        }
        if (candidate.length() < USERNAME_MIN) {
            throw new IllegalArgumentException("Username must be at least " + USERNAME_MIN + " characters");
        }
        if (candidate.length() > USERNAME_MAX) {
            throw new IllegalArgumentException("Username must be at most " + USERNAME_MAX + " characters");
        }
        if (!candidate.matches(USERNAME_REGEX)) {
            throw new IllegalArgumentException("Username contains invalid characters (allowed: letters, numbers, dot, underscore, dash)");
        }

        Optional<User> existingByUsername = userRepository.findByUsername(candidate);
        if (existingByUsername.isPresent() && !existingByUsername.get().getId().equals(userId)) {
            throw new UsernameConflictException("Username is already taken");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setUsername(candidate);
        return userRepository.save(user);
    }
}