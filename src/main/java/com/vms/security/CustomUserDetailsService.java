package com.vms.security;

import com.vms.entity.User;
import com.vms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom {@link UserDetailsService} implementation that loads user credentials
 * and authorities from the application database.
 *
 * <p>Users are looked up by email address. The granted authority is derived
 * from the user's {@link com.vms.enums.Role} prefixed with {@code ROLE_}.</p>
 *
 * @see com.vms.filter.JwtAuthenticationFilter
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user's authentication details by their email address.
     *
     * @param email the user's email address (used as the username)
     * @return a {@link UserDetails} instance containing credentials and authorities
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
