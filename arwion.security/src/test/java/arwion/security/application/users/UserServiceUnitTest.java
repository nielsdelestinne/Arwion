package arwion.security.application.users;

import arwion.security.application.exceptions.UsernameNotUniqueException;
import arwion.security.domain.model.users.User;
import arwion.security.domain.model.users.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("When Register User")
    class RegisterUser {

        @Test
        @DisplayName("Given a NON existing username, Then register user and return its id")
        void registerUser_givenNonExistingUsername_thenReturnId() {
            when(userRepository.findByUsername("Gert"))
                    .thenReturn(Optional.empty());
            User expectedPersistedUser = new User("Gert", "xyz", List.of("ADMIN"));
            when(userRepository.save(any(User.class)))
                    .thenReturn(expectedPersistedUser);

            var actualId = userService.registerUser("Gert", "xyz", List.of("ADMIN"));

            assertNotNull(actualId);
            assertEquals(expectedPersistedUser.getId(), actualId);
        }

        @Test
        @DisplayName("Given an existing username, Then throw Exception")
        void registerUser_givenExistingUsername_thenThrowException() {
            when(userRepository.findByUsername("Gert"))
                    .thenReturn(Optional.of(new User("Gert", "xyz", null)));

            Executable methodToExecute =
                    () -> userService.registerUser("Gert", "xyz", List.of("ADMIN"));

            assertThrows(UsernameNotUniqueException.class, methodToExecute);
        }
    }

    @Nested
    @DisplayName("When Load User by username")
    class LoadUserByUsername {

        @Test
        @DisplayName("Given an existing username, Then return the found UserDetails")
        void loadUserByUsername_givenExistingUsername_thenReturnUserDetails() {
            var expectedUserDetails = new User("Gert", "xyz", null);
            when(userRepository.findByUsername("Gert"))
                    .thenReturn(Optional.of(expectedUserDetails));

            var actualUserDetails = userService.loadUserByUsername("Gert");

            assertEquals(expectedUserDetails, actualUserDetails);
        }

        @Test
        @DisplayName("Given an NON existing username, Then throw Exception")
        void loadUserByUsername_givenNonExistingUsername_thenThrowException() {
            when(userRepository.findByUsername("Gert"))
                    .thenReturn(Optional.empty());

            Executable methodToExecute = () -> userService.loadUserByUsername("Gert");

            assertThrows(UsernameNotFoundException.class, methodToExecute, "Timmyyyy");
        }
    }


}