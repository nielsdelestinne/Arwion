package arwion;

import arwion.security.domain.model.users.User;
import arwion.security.domain.model.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication(scanBasePackages = {
    "arwion.security"
})
public class Application implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        userRepository.save(new User("niels", "{noop}password", List.of("ADMIN")));
    }
}
