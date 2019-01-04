package arwion;

import arwion.security.domain.model.users.User;
import arwion.security.domain.model.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"arwion.security"})
public class IntegrationTestConfiguration {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void persistDummyUsers() {
        userRepository
                .save(new User("FakeAdminUser", passwordEncoder.encode("FakeAdminPassword"), List.of("ADMIN")));
        userRepository
                .save(new User("FakeNormalUser", passwordEncoder.encode("FakeNormalPassword"), List.of("USER")));
    }

}
