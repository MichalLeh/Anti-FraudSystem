package antifraud.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    /**
     * Configure the {@link AuthenticationManagerBuilder} to specify which UserDetailsService and
     * {@link PasswordEncoder} to use.
     *
     * @param auth The {@link AuthenticationManagerBuilder} to use
     * @throws Exception If an error occurs
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }
    /**
     * Configure the security of the web application by restricting access based on the HttpServletRequest.
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
            .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error
            .and()
            .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
            .and()
            .authorizeRequests() // manage access
            .mvcMatchers("/api/auth/user/*", "/api/auth/access", "/api/auth/role").hasAuthority("ROLE_ADMINISTRATOR")
            .mvcMatchers("/api/auth/list").hasAnyAuthority("ROLE_ADMINISTRATOR", "ROLE_SUPPORT")
            .mvcMatchers("/api/antifraud/transaction").hasAuthority("ROLE_MERCHANT")
            .antMatchers("/actuator/shutdown").permitAll() // needs to run test
            .anyRequest().permitAll()
            // other matchers
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    /**
     * Encrypt the password of the user using the BCryptPasswordEncoder.
     *
     * @return the {@link PasswordEncoder} to use
     */
    @Bean
    PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
