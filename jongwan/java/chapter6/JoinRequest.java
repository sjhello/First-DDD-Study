package chapter6;

import lombok.Getter;

@Getter
public class JoinRequest {
    private String email;
    private String password;

    public JoinRequest(String email, String password) {
        this.email = email;
        this.password = encrptPassword(password);
    }
    private String encrptPassword(String password) {
        return password+"1234566666";
    }
}
