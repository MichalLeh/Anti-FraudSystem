package antifraud.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
/**
 * This class implements the UserDetails interface and is used by Spring Security.
 */
public class UserDetailsImpl implements UserDetails {
//    private final String username;
//    private final String password;
//    private final List<GrantedAuthority> rolesAndAuthorities;
//    public UserDetailsImpl(User user) {
//        this.username = user.getEmail();
//        this.password = user.getPassword();
//        List<String> recipesAuthoredList= new ArrayList<>();
//        this.rolesAndAuthorities = List.of(new SimpleGrantedAuthority(recipesAuthoredList.toString()));
//    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
    @Override
    public String getPassword() {
        return null;
    }
    @Override
    public String getUsername() {
        return null;
    }
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
    @Override
    public boolean isEnabled() {
        return false;
    }
}
