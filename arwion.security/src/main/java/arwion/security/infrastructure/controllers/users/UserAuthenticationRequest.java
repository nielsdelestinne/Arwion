package arwion.security.infrastructure.controllers.users;

public class UserAuthenticationRequest {

    private String username;
    private String password;

    public UserAuthenticationRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserAuthenticationRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
