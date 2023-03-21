package antifraud.persistance;

import antifraud.model.IPAddress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * Interface is used to interact with the user table in the database.
 */
@Repository
public interface IPRepository extends CrudRepository<IPAddress, Long> {
    Optional<IPAddress> findByIpIgnoreCase(String ip);
    List<IPAddress> findAllByOrderById();
}
