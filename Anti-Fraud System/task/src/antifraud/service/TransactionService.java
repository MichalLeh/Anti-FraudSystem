package antifraud.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TransactionService {
    public TransactionService() {
    }
    /**
     * Process a transaction.
     *
     * @param amount Transaction to be processed
     * @return A response entity with the status of the transaction
     */
    public ResponseEntity processTransaction(Long amount) {
        try {
            if (amount <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else if (amount <= 200) {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", "ALLOWED"));
            } else if (amount <= 1500) {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", "MANUAL_PROCESSING"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", "PROHIBITED"));

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
