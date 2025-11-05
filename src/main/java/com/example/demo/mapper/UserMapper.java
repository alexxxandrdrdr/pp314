package com.example.demo.mapper;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.UserEditDto;
import com.example.demo.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleRepository roleRepository;

    public UserMapper(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public User mapDtoToUser (UserEditDto userEditDto) {
        User user = new User();
        user.setId(userEditDto.getId());
        user.setFirstname(userEditDto.getFirstname());
        user.setLastname(userEditDto.getLastname());
        user.setEmail(userEditDto.getEmail());
        user.setPassword(userEditDto.getPassword());
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
