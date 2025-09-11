package com.example.demo.service;


import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserEditDto;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findById(Long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void updateUser(UserEditDto userEditDto, Long id) {
        if (userEditDto.getId() != null && id != null) {
            if (!id.equals(userEditDto.getId())) {
                throw new IllegalArgumentException("ID в пути не совпадает с ID пользователя");
            }
            User user = findById(id);
            if (user == null) {
                throw new EntityNotFoundException("Пользователь с ID " + id + " не найден");
            }
            user.setFirstname(userEditDto.getFirstName());
            user.setLastname(userEditDto.getLastName());
            user.setAge(userEditDto.getAge());
            user.setEmail(userEditDto.getEmail());

            if (userEditDto.getPassword() != null && !userEditDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userEditDto.getPassword()));
            }
            Set<Role> roles = userEditDto.getRolesIds().stream()
                    .map(roleService::findById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            user.setRoles(roles);

            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Передан пустой пользователь");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
