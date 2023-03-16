package antifraud.persistance;

import antifraud.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface is used to interact with the user table in the database.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    List<User> findAllByOrderById();
}
