package antifraud.service;

import antifraud.model.ERole;
import antifraud.model.User;
import antifraud.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    /**
     * Add new user.
     *
     * @param user New user to be added
     * @return A response entity with the status of the addition
     */
    public ResponseEntity add(User user){
        if(userRepository.count() == 0) {
            user.setRole(ERole.ROLE_ADMINISTRATOR.label);
            user.setAccountNonLocked(true);
        }
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }
    /**
     * Change of user's role.
     *
     * @param request JSON body containing username and role
     * @return A response entity with the status of the role change
     */
    public ResponseEntity<?> changeRole(Map<String, String> request) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(request.get("username"));

        if(!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(user.get().getRole().equals(ERole.ROLE_ADMINISTRATOR.label)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!request.get("role").equals(ERole.ROLE_SUPPORT.label) && !request.get("role").equals(ERole.ROLE_MERCHANT.label)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (user.get().getRole().equals(request.get("role"))) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        user.get().setRole(request.get("role"));
        userRepository.save(user.get());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    /**
     * Change of user's access.
     *
     * @param request JSON body containing username and operation
     * @return A response entity with the status of the access change
     */
    public ResponseEntity<?> changeAccess(Map<String, String> request) {
        Optional<User> user = userRepository.findByUsernameIgnoreCase(request.get("username"));

        if(!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(user.get().getRole().equals(ERole.ROLE_ADMINISTRATOR.label) && request.get("operation").equals("LOCK")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        user.get().setAccountNonLocked(request.get("operation").equals("UNLOCK"));
        userRepository.save(user.get());

        return new ResponseEntity<>(Map.of("status", String.format("User %s %s!", user.get().getUsername(), user.get().isAccountNonLocked() ? "unlocked":"locked")), HttpStatus.OK);
    }
    /**
     * Find user.
     *
     * @param username The username to be used to retrieve the user
     * @return User or null if username not found
     */
    public Optional<User> findUser(String username){
        return userRepository.findByUsernameIgnoreCase(username);
    }
    /**
     * Finad all user(s).
     *
     * @return A list of all users in database
     */
    public List<User> findAll(){
        return userRepository.findAllByOrderById();
    }
    /**
     * Delete user.
     *
     * @param username User to be deleted
     * @return A response entity with the status of the deletion
     */
    public ResponseEntity deleteUser(String username){
        User user = userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userRepository.delete(user);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("username", user.getUsername(), "status", "Deleted successfully!"));
    }

}
