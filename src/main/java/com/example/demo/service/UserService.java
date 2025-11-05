package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserEditDto;

import java.util.List;
import java.util.Optional;


public interface UserService {
    List<User> findAll();

    void saveUser(User user);

    Optional<User> findById(Long id);

    void updateUser(UserEditDto userEditDto, Long id);

    void updateUser(Long id, String firstname, String lastname, byte age, String email, String password, List<Long> roles);

    void deleteUser(Long id);

    void saveUserFromDto(UserEditDto userEditDto);
}
