package com.fundraising.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fundraising.dto.AdminDto;
import com.fundraising.dto.UserDto;
import com.fundraising.exception.ResourceNotFoundException;
import com.fundraising.service.AdminService;
import com.fundraising.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AuthenticationProvider {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String login, Long userId, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 24 hours

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(login)
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .sign(algorithm);
    }

    public Authentication validateToken(String token) throws
            ResourceNotFoundException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        AdminDto adminDto = null;
        UserDto userDto = null;

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);
        String role = decoded.getClaim("role").asString();
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (null != role) {
            if (role.equals("ROLE_ADMIN")) {
                adminDto = adminService.findByEmail(decoded.getSubject());
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                adminDto.setAuthorities(authorities);
                adminDto.setUsername(decoded.getSubject());
            } else if (role.equals("ROLE_USER")) {
                userDto = userService.findByEmail(decoded.getSubject());
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                userDto.setAuthorities(authorities);
                userDto.setUsername(decoded.getSubject());
            }
        } else System.out.println(role + " role is not found");

        assert role != null;
        return new UsernamePasswordAuthenticationToken(role.equals("ROLE_ADMIN")
                ? adminDto : role.equals("ROLE_USER") ? userDto : null, null, Collections.emptyList());
    }
}
