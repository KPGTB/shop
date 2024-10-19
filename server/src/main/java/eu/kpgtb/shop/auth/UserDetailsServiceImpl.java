package eu.kpgtb.shop.auth;

import eu.kpgtb.shop.data.entity.UserEntity;
import eu.kpgtb.shop.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userRepository.findByEmail(username);
        if(user == null) throw new UsernameNotFoundException(username);
        return new User(user);
    }
}
