package antifraud.persistance;

import antifraud.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface is used to interact with the transacton table in the database.
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
//    List<Transaction> findAllByOrderByNumber(String number);
    List<Transaction> findByNumberIgnoreCaseOrderByIdDesc(String number);
}
