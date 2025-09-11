package com.example.demo.service;

import com.example.demo.model.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface RoleService {

    List<Role> findAll();

    Role findById(Long id);

    String rolesToString(Collection<? extends GrantedAuthority> authorities);
}
