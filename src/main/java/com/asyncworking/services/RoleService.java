package com.asyncworking.services;

import com.asyncworking.exceptions.RoleNotFoundException;
import com.asyncworking.models.Role;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void assignRole(UserEntity user, String roleName) {
        Role role = fetchRoleByName(roleName);
        user.getRoles().add(role);
    }

    private Role fetchRoleByName (String roleName) {
       return roleRepository
               .findByName(roleName)
               .orElseThrow(() -> new RoleNotFoundException(roleName + " does not exist!"));
    }
}
