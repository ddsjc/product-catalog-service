package sukhov.danila.domain.entities;

import java.io.Serializable;

public class UserEntity implements Serializable {
    private String username;
    private String passwordHash;
    private ERole role;

    public UserEntity(String username, String password, ERole role) {
        this.username = username;
        this.passwordHash = password;
        this.role = role;
    }

    public String getUsername() {return username;}
    public String getPasswordHash() {return passwordHash;}
    public ERole getRole() {return role;}

}
