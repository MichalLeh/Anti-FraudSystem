package antifraud.security;

import antifraud.model.ERole;
import antifraud.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * This class implements the UserDetails interface and is used by Spring Security.
 */
public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String password;
    private final List<GrantedAuthority> rolesAndAuthorities;
    private final boolean isAccountNonLocked;
    public UserDetailsImpl(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        rolesAndAuthorities = List.of(new SimpleGrantedAuthority(ERole.valueOfLabel(user.getRole()).toString()));
        isAccountNonLocked = user.isAccountNonLocked();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }
    //UNUSED METHODS BELOW
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
