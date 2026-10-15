package ru.loginov.dev.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.loginov.dev.dto.auth.SignInRequest;
import ru.loginov.dev.dto.auth.SignUpRequest;
import ru.loginov.dev.dto.auth.TokenResponse;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.enums.Role;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public TokenResponse signIn(SignInRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsernameOrEmail(),
                                request.getPassword()
                        )
                );

        String token = jwtService.generateToken(authentication);

        return new TokenResponse(token);
    }

    public User signUp(SignUpRequest request) {
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                Role.USER
        );

        return userService.create(user);
    }
}
