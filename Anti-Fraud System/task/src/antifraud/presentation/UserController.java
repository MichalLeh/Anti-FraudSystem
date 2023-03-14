package antifraud.presentation;

import antifraud.model.User;
import antifraud.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {
    private AuthenticationService authenticationService;
    private PasswordEncoder encoder;
    public UserController(AuthenticationService authenticationService, PasswordEncoder encoder) {
        this.authenticationService = authenticationService;
        this.encoder = encoder;
    }
    @PostMapping("/api/auth/user")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity registerUser(@RequestBody @Valid User user) {
        if (!authenticationService.checkIfUserExists(user.getUsername())) {
            authenticationService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
