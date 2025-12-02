package sukhov.danila.in.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sukhov.danila.domain.services.AuthService;
import sukhov.danila.dtos.CreateUserDTO;
import sukhov.danila.dtos.UserDTO;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    public static final String SIGN_UP = "/api/auth/register";
    public static final String SIGN_IN = "/api/auth/login";

    @PostMapping(SIGN_UP)
    public UserDTO registerController(@RequestBody CreateUserDTO userDTO){
        return  authService.register(userDTO.getUsername(), userDTO.getPassword(), userDTO.getRole());
    }

    @PostMapping(SIGN_IN)
    public UserDTO loginController(@RequestBody UserDTO userDTO){
        return  authService.login(userDTO.getUsername(), userDTO.getPasswordHash());
    }
}
