package antifraud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name="TRANSACTIONS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Min(1)
    private Long amount;
    @NotEmpty
    private String ip;
    @NotEmpty
    private String number;
    @NotEmpty
    private String region;
    @NotNull
    private LocalDateTime date = LocalDateTime.now();
}
