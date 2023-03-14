package antifraud.service;

import antifraud.persistance.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import antifraud.model.User;
@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User registerUser(User user){
        return userRepository.save(user);
    }
    public Boolean checkIfUserExists(String username){
        return userRepository.existsByUsernameIgnoreCase(username);
    }
}
