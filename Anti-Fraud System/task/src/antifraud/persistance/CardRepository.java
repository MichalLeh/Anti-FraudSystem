package antifraud.persistance;

import antifraud.model.Card;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends CrudRepository<Card, Long> {
    Optional<Card> findByNumberIgnoreCase(String number);
    List<Card> findAllByOrderById();
}
