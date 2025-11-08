package com.example.demo.mapper;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.dto.UserEditDto;
import com.example.demo.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserMapper(RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User mapDtoToUser (UserEditDto userEditDto) {
        User user = new User();
        user.setId(userEditDto.getId());
        user.setFirstname(userEditDto.getFirstname());
        user.setLastname(userEditDto.getLastname());
        user.setEmail(userEditDto.getEmail());
        user.setPassword(passwordEncoder.encode(userEditDto.getPassword()));
        user.setAge(userEditDto.getAge());
        user.setRoles(userEditDto.getRoles().stream().map(roleRepository::findById).flatMap(Optional::stream).collect(Collectors.toSet()));
        return user;
    }
    public UserEditDto mapUserToUserEditDto (Optional<User> inputUser) {
        User user = inputUser.orElseThrow(IllegalArgumentException::new);
        UserEditDto userEditDto = new UserEditDto();
        userEditDto.setId(user.getId());
        userEditDto.setFirstname(user.getFirstname());
        userEditDto.setLastname(user.getLastname());
        userEditDto.setEmail(user.getEmail());
        userEditDto.setPassword(user.getPassword());
        userEditDto.setAge(user.getAge());
        userEditDto.setRoles(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
        return userEditDto;
    }
}
