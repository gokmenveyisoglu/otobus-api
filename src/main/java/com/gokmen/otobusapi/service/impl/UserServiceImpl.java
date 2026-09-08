package com.gokmen.otobusapi.service.impl;

import com.gokmen.otobusapi.repository.UserRepository;
import com.gokmen.otobusapi.repository.entities.User;
import com.gokmen.otobusapi.repository.record.User.CreateUser;
import com.gokmen.otobusapi.repository.record.User.ResponseUser;
import com.gokmen.otobusapi.repository.record.User.UpdateUser;
import com.gokmen.otobusapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll().stream().filter(User::isActive).toList();
    }

    @Override
    @Transactional
    public ResponseUser setUser(CreateUser request) {
        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());

        User savedUser = userRepository.save(user);
        return User.toResponse(savedUser);
    }

    @Override
    @Transactional
    public ResponseUser updateEmail(UpdateUser request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setEmail(request.email());

        User savedUser = userRepository.save(user);
        return User.toResponse(savedUser);
    }

    @Override
    public ResponseUser deactivateUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setActive(false);
        User deactivatedUser = userRepository.save(user);
        return User.toResponse(deactivatedUser);
    }
}
