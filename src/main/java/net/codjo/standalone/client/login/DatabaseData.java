package net.codjo.standalone.client.login;
/**
 *
 */
public class DatabaseData {
    private String url;
    private String catalog;
    private String login;
    private String password;


    public DatabaseData(String url, String catalog, String login, String password) {
        this.url = url;
        this.catalog = catalog;
        this.login = login;
        this.password = password;
    }


    public String getUrl() {
        return url;
    }


    public String getCatalog() {
        return catalog;
    }


    public String getLogin() {
        return login;
    }


    public String getPassword() {
        return password;
    }
}
