package antifraud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * This class represents a user. It is used for authentication and authorization.
 */
@Entity
@Table(name="USERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role = ERole.ROLE_MERCHANT.label;
    @JsonIgnore
    private boolean isAccountNonLocked = false;
}
