package arwion.security.application.exceptions;

public class UsernameNotUniqueException extends RuntimeException {

    public UsernameNotUniqueException(String username) {
        super(String.format("The provided username \'%s\' already exists", username));
    }
}
