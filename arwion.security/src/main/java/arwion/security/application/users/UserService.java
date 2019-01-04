package arwion.security.application.users;

import arwion.security.application.exceptions.UsernameNotUniqueException;
import arwion.security.domain.model.users.User;
import arwion.security.domain.model.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "CustomUserService")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }

    public String registerUser(String username, String saltedAndHashedPassword, List<String> roles) throws UsernameNotUniqueException{
        AssertUsernameIsUnique(username);
        return userRepository
                .save(new User(username, saltedAndHashedPassword, roles))
                .getId();
    }

    private void AssertUsernameIsUnique(String username) throws UsernameNotUniqueException{
        if(this.userRepository.findByUsername(username).isPresent()) {
            throw new UsernameNotUniqueException(username);
        }
    }

}
