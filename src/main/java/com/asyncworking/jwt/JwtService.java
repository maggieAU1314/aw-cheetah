package com.asyncworking.jwt;

import com.asyncworking.auth.ApplicationUserService;
import com.asyncworking.auth.AwcheetahGrantedAuthority;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.ProjectUserRepository;
import com.google.gson.internal.LinkedTreeMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretKey secretKey;
    private final ApplicationUserService applicationUserService;

    private final EmployeeRepository employeeRepository;
    private final ProjectUserRepository projectUserRepository;

    public String creatJwtToken(String email) {
        UserDetails user = applicationUserService.loadUserByUsername(email);
        UserEntity userEntity = applicationUserService.mapToUserDetails(email);
        Set<Long> companyIds = employeeRepository.findCompanyIdByUserId(userEntity.getId());
        Set<Long> projectIds = projectUserRepository.findProjectIdByUserId(userEntity.getId());
        String jwtToken = Jwts.builder()
                .setSubject(email)
                .claim("authorities", user.getAuthorities())
                .claim("companyIds", companyIds)
                .claim("projectIds", projectIds)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
                .signWith(secretKey)
                .compact();
        return jwtToken;
    }

    public JwtDto refreshJwtToken(String auth) {
        String oldToken = auth.replace("Bearer ", "");

        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(oldToken);
        Claims body = claimsJws.getBody();
        String email = body.getSubject();

        var authorities = (List<LinkedTreeMap<String, Object>>) body.get("authorities");

        UserDetails user = applicationUserService.loadUserByUsername(email);
        if (authorities.size() == user.getAuthorities().size()) {
            return JwtDto.builder()
                    .accessToken(oldToken)
                    .message("No need to refresh the jwtToken.")
                    .build();
        }

        String newToken = creatJwtToken(email);
        return JwtDto.builder()
                .accessToken(newToken)
                .message("JwtToken has already refreshed.")
                .build();
    }
}
