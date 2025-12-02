package com.example.demo.service;


import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.dto.UserEditDto;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserEditDto getUserEditDtoById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return userMapper.mapUserToUserEditDto(user);
    }

    @Transactional
    @Override
    public void updateUser(Long id, String firstname, String lastname,
                           byte age, String email, String password,
                           List<Long> roles) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            try {
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setAge(age);
                user.setEmail(email);
                if (password != null && !password.isEmpty()) {
                    user.setPassword(passwordEncoder.encode(password));
                }
                user.setRoles(roles.stream().map(roleService::findById).collect(Collectors.toSet()));
                userRepository.save(user);
            } catch (DataAccessException e) {
                log.error(e.getMessage());
            }

        }


    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void saveUserFromDto(UserEditDto userEditDto) {
        userRepository.save(userMapper.mapDtoToUser(userEditDto));
    }
}
