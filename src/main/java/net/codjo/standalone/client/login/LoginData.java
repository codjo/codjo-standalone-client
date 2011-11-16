package net.codjo.standalone.client.login;
/**
 *
 */
public class LoginData {
    private String login;
    private String password;


    public LoginData(String login, String password) {
        this.login = login;
        this.password = password;
    }


    public String getLogin() {
        return login;
    }


    public String getPassword() {
        return password;
    }
}
