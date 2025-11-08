package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.dto.UserEditDto;

import java.util.List;
import java.util.Optional;


public interface UserService {
    List<User> findAll();

    Optional<User> findById(Long id);

    UserEditDto getUserEditDtoById(Long id);

    void updateUser(Long id, String firstname, String lastname, byte age, String email, String password, List<Long> roles);

    void deleteUser(Long id);

    void saveUserFromDto(UserEditDto userEditDto);
}
