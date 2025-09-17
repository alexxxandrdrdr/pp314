package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserEditDto;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;


public interface UserService {
    List<User> findAll();

    void saveUser(User user);

    User findById(Long id) throws EntityNotFoundException;

    void updateUser(UserEditDto userEditDto, Long id);

    void deleteUser(Long id);

    void saveUserFromDto(UserEditDto userEditDto);
}
