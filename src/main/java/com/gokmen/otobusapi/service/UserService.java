package com.gokmen.otobusapi.service;

import com.gokmen.otobusapi.repository.entities.User;
import com.gokmen.otobusapi.repository.record.User.CreateUser;
import com.gokmen.otobusapi.repository.record.User.ResponseUser;
import com.gokmen.otobusapi.repository.record.User.UpdateUser;

import java.util.List;

public interface UserService {
    List<User> getUsers();
    ResponseUser setUser(CreateUser request);
    ResponseUser updateEmail(UpdateUser request);
    ResponseUser deactivateUser(int userId);
}
