package com.gokmen.otobusapi.controller;

import com.gokmen.otobusapi.repository.entities.User;
import com.gokmen.otobusapi.repository.record.User.CreateUser;
import com.gokmen.otobusapi.repository.record.User.ResponseUser;
import com.gokmen.otobusapi.repository.record.User.UpdateUser;
import com.gokmen.otobusapi.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getUsers() {
        return userService.getUsers();
    }
    @PostMapping()
    public ResponseUser setUser(@RequestBody CreateUser request) {
        return userService.setUser(request);
    }
    @PutMapping()
    public ResponseUser updateEmail(@RequestBody UpdateUser request) {
        return userService.updateEmail(request);
    }
    @PatchMapping("{id}")
    public ResponseUser deactivateUser(@PathVariable("id") int userId) {
        return userService.deactivateUser(userId);
    }
}
