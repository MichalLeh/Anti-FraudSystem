package antifraud.persistance;

import antifraud.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface is used to interact with the user table in the database.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsernameIgnoreCase(String username);
}
