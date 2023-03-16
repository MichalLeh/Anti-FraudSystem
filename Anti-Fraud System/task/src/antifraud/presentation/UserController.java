package antifraud.presentation;

import antifraud.model.User;
import antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Add user.
     *
     * @param user User to be added to the database
     * @return A response entity with the status of the addition
     */
    @PostMapping("/user")
    public ResponseEntity postUser(@RequestBody @Valid User user) {
        return userService.add(user);
    }
    /**
     * Get user(s).
     *
     * @return A list of all users in database
     */
    @GetMapping("/list")
    public List<User> getUser(){
        return userService.findAll();
    }
    /**
     * Delete user.
     *
     * @param username User to be deleted
     * @return A response entity with the status of the deletion
     */
    @DeleteMapping("/user/{username}")
    public ResponseEntity deleteUser(@PathVariable("username") String username){
        return userService.deleteUser(username);
    }
    /**
     * Change of user's role.
     *
     * @param request JSON body containing username and role
     * @return A response entity with the status of the role change
     */
    @PutMapping("/role")
    public ResponseEntity<?> changeRole(@RequestBody @Valid Map<String, String> request) {
        return userService.changeRole(request);
    }
    /**
     * Change of user's access.
     *
     * @param request JSON body containing username and operation
     * @return A response entity with the status of the access change
     */
    @PutMapping("/access")
    public ResponseEntity<?> changeAccess(@RequestBody @Valid Map<String, String> request) {
        return userService.changeAccess(request);
    }
}
