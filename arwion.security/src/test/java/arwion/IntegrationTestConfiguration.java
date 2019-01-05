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

    public static final String FAKE_NORMAL_USER_USERNAME = "FakeNormalUser";
    public static final String FAKE_ADMIN_USER_PASSWORD = "FakeAdminPassword";
    public static final String FAKE_NORMAL_USER_PASSWORD = "FakeNormalPassword";
    public static final String FAKE_ADMIN_USER_USERNAME = "FakeAdminUser";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void persistDummyUsers() {
        userRepository
                .save(new User(FAKE_ADMIN_USER_USERNAME, passwordEncoder.encode(FAKE_ADMIN_USER_PASSWORD), List.of("ADMIN", "USER")));
        userRepository
                .save(new User(FAKE_NORMAL_USER_USERNAME, passwordEncoder.encode(FAKE_NORMAL_USER_PASSWORD), List.of("USER")));
    }

}
