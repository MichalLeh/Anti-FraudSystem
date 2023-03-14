package antifraud.service;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public TransactionService() {
    }
    public String checkTransaction(Long amount) {
        if (amount > 1500) {
            return "PROHIBITED";
        } else if (amount > 200) {
            return "MANUAL_PROCESSING";
        }
        return "ALLOWED";
    }
}
