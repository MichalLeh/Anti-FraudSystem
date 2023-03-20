package antifraud.presentation;

import antifraud.model.IPAddress;
import antifraud.model.Transaction;
import antifraud.model.User;
import antifraud.security.UserDetailsImpl;
import antifraud.service.TransactionService;
import antifraud.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    private TransactionService transactionService;
    private UserService userService;
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }
    /**
     * Process a transaction.
     *
     * @param transaction Transaction to be processed
     * @param userDetails The user's authentication parameters that is processing the transaction
     * @return A response entity with the status of the transaction
     */
    @PostMapping("/transaction")
    public ResponseEntity processTransaction(@RequestBody @Valid Transaction transaction, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String userName = userDetails.getUsername();
        Optional<User> currentUser = userService.findUser(userName);

        if(currentUser.isPresent()) {
            Long amount = transaction.getAmount();
            return transactionService.processTransaction(amount);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    /**
     * Process an IP address.
     *
     * @param ip IP address to be processed
     * @return A response entity with the status of the processing
     */
    @PostMapping("/suspicious-ip")
    public ResponseEntity addIp(@RequestBody @Valid IPAddress ip) {
        return transactionService.addSuspiciousIP(ip);
    }
    /**
     * Delete a specific IP address.
     *
    // * @param userDetails The user's authentication parameters that is deleting the recipe
     * @param ip          The IP address to be deleted
     * @return A response entity with the status of the deletion
     */
    @DeleteMapping("/suspicious-ip/{ip}")
    //@AuthenticationPrincipal UserDetails userDetails
    public ResponseEntity deleteIp(@PathVariable String ip) {
        return transactionService.deleteSuspiciousIP(ip);
    }
    /**
     * Get IP addresse(s).
     *
     * @return A list of all IP addresses in database
     */
    @GetMapping("/suspicious-ip")
    public List<IPAddress> getIPAddresses() {
        return transactionService.findAll();
    }
}
