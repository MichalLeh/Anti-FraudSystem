package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter

public class Transaction {
    @NotNull
    private Long amount;
    public Transaction(){
    }
    public Transaction(Long amount) {
        this.amount = amount;
    }
//    public Long getAmount() {
//        return amount;
//    }
//    public void setAmount(Long amount) {
//        this.amount = amount;
//    }
}
