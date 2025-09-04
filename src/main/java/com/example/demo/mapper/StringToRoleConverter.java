package com.example.demo.mapper;

import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRoleConverter implements Converter<String, Role> {
    private final RoleRepository roleRepository;

    public StringToRoleConverter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role convert(String source) {
        Long id = Long.valueOf(source);
        return roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Роль не найдена: " + id));
    }
}