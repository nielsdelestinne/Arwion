package arwion.security.infrastructure.controllers.users;

import java.util.List;

public class UserRegistrationRequest {

    private String username;
    private String password;
    private List<String> roles;

    public UserRegistrationRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserRegistrationRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserRegistrationRequest setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }
}
