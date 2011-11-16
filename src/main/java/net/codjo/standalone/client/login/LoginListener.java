package net.codjo.standalone.client.login;
import net.codjo.agent.UserId;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
/**
 *
 */
public interface LoginListener {
    enum FailureCause {
        BAD_LOGIN,
        SERVICE_ERROR,
        TECHNICAL_ERROR,
        GRANT_ERROR,
        ACCOUNT_LOCKED
    }


    public void loginOk(UserId userId, ConnectionPool pool, User user);


    public void loginFailed(FailureCause cause, Throwable error);
}
