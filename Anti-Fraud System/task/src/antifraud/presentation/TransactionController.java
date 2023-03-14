package antifraud.presentation;

import antifraud.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/api/antifraud/transaction")
    public ResponseEntity postTransaction(@RequestBody Map<String, Long> transaction) {

        if (transaction.get("amount" ) != null) {
            Long amount = transaction.get("amount");
            if (amount > 0) {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", transactionService.checkTransaction(amount)));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
