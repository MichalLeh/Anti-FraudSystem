package antifraud.service;

import antifraud.model.Card;
import antifraud.model.IPAddress;
import antifraud.model.Transaction;
import antifraud.persistance.CardRepository;
import antifraud.persistance.IPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class TransactionService {
    @Autowired
    private IPRepository ipRepository;
    @Autowired
    private CardRepository cardRepository;
    public TransactionService(IPRepository ipRepository, CardRepository cardRepository) {
        this.ipRepository = ipRepository;
        this.cardRepository = cardRepository;
    }
    /**
     * Add suspicious IP address.
     *
     * @param ip IPAddress to be added
     * @return A response entity with the status of the addition
     */
    public ResponseEntity addSuspiciousIP(IPAddress ip) {
        if (ipRepository.findByIpIgnoreCase(ip.getIp()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(ipRepository.save(ip));
    }
    /**
     * Delete suspicious IP address.
     *
     * @param ip IP address to be deleted
     * @return A response entity with the status of the deletion
     */
    public ResponseEntity deleteSuspiciousIP(String ip) {
        if(!Pattern.matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$", ip)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        IPAddress ipAddress = ipRepository.findByIpIgnoreCase(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ipRepository.delete(ipAddress);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", String.format("IP %s successfully removed!", ipAddress.getIp())));
    }
    /**
     * Get all IP addresses.
     *
     * @return A list with all the IP addresses in database
     */
    public List<IPAddress> findAllIPs() {
        return ipRepository.findAllByOrderById();
    }
    /**
     * Add stolen card.
     *
     * @param card Card to be added
     * @return A response entity with the status of the addition
     */
    public ResponseEntity addStolenCard(Card card) {
        if (!isCardValid(card.getNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (cardRepository.findByNumberIgnoreCase(card.getNumber()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(cardRepository.save(card));
    }
    /**
     * Delete stolen card.
     *
     * @param number Card to be deleted
     * @return A response entity with the status of the deletion
     */
    public ResponseEntity deleteStolenCard(String number) {
        if (!isCardValid(number)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Card card = cardRepository.findByNumberIgnoreCase(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        cardRepository.delete(card);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", String.format("Card %s successfully removed!", card.getNumber())));
    }
    /**
     * Get all cards.
     *
     * @return A list with all the cards in database
     */
    public List<Card> findAllCards() {
        return cardRepository.findAllByOrderById();
    }
    /**
     * Process a transaction.
     *
     * @param transaction Transaction to be processed
     * @return A response entity with the status of the process
     */
    public ResponseEntity processTransaction(Transaction transaction) {
        if (!isCardValid(transaction.getNumber()) || !isPatternValid(transaction.getIp())) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> violation = new ArrayList<>();

        String amountIs = processAmount(transaction.getAmount());

        if (amountIs.equals("PROHIBITED")) {
            violation.add("amount");
        }
        if (cardRepository.findByNumberIgnoreCase(transaction.getNumber()).isPresent()) {
            violation.add("card-number");
            amountIs = "PROHIBITED";
        }
        if (ipRepository.findByIpIgnoreCase(transaction.getIp()).isPresent()) {
            violation.add("ip");
            amountIs = "PROHIBITED";
        }
        // if the amount yield MANUAL_PROCESSING but the final result is PROHIBITED
        // then 'amount' should not be listed as a reason for the 'info'
        if (violation.isEmpty()) {
            if (amountIs.equals("MANUAL_PROCESSING")) {
                violation.add("amount");
            } else {
                violation.add("none");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", amountIs, "info", String.join(", ", violation)));
    }
    /**
     * Process an amount.
     *
     * @param amount Amount to be checked
     * @return String based on the amount
     */
    private String processAmount(long amount) {
        if (amount <= 200) {
            return "ALLOWED";
        } else if (amount <= 1500) {
            return "MANUAL_PROCESSING";
        }
        return "PROHIBITED";
    }
    /**
     * Check if card is valid.
     *
     * @param number Number to be checked by Luhn's algorithm
     * @return True if the algorithm is passed, otherwise false
     */
    private boolean isCardValid(String number) {
        int result = 0;
        // Luhn algorithm
        for(int i = 0; i < number.length(); ++i) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == 0) {
                int doubleDigit = digit * 2 > 9 ? digit * 2 - 9 : digit * 2;
                result += doubleDigit;
            } else {
                result += digit;
            }
        }
        return result % 10 == 0;
    }
    /**
     * Check if ip address is valid.
     *
     * @param ipAddress IP address to be checked by regex
     * @return True if Ip match regex, otherwise false
     */
    private Boolean isPatternValid(String ipAddress) {
        return Pattern.matches("^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$", ipAddress);
    }
}
