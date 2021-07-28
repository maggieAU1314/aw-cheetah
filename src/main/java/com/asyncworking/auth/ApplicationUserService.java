package com.asyncworking.auth;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.asyncworking.models.Authority;
import com.asyncworking.models.Role;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.RoleRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity user = mapToUserDetails(email);

        Set<Role> roles = userRoleRepository.findRoleIdByUserId(user.getId()).stream()
                .map(roleId -> roleRepository.findById(roleId).get())
                .collect(Collectors.toSet());

        Set<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(roles);

        return new User(user.getEmail(),
                user.getPassword().replaceAll("\\s+", ""),
                grantedAuthorities);
    }

    private UserEntity mapToUserDetails(String email) {
        return userRepository.findUserEntityByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", email)));
    }

    private Set<GrantedAuthority> getGrantedAuthorities(Set<Role> roles) {
        Set<Authority> authorities = new HashSet<>();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : roles) {
            authorities.addAll(role.getAuthorities());
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        authorities.forEach(authority -> grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName())));

        return grantedAuthorities;
    }
}

