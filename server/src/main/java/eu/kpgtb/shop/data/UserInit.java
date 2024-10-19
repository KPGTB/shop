package eu.kpgtb.shop.data;

import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.data.entity.UserEntity;
import eu.kpgtb.shop.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserInit implements CommandLineRunner {
    @Autowired
    private Properties properties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(this.userRepository.existsByEmail(this.properties.getBusinessAccountEmail())) return;

        this.userRepository.save(new UserEntity(
            this.properties.getBusinessAccountEmail(),
            this.passwordEncoder.encode(this.properties.getBusinessAccountDefaultPassword()),
                UserEntity.UserRole.BUSINESS
        ));
    }
}
