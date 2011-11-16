package net.codjo.standalone.client.login;
import net.codjo.agent.UserId;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.standalone.client.StandaloneGuiCore;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
/**
 *
 */
public class DefaultLoginListener implements LoginListener {
    private final Logger logger = Logger.getLogger(getClass());
    private final AbstractLoginWindow loginWindow;
    private final StandaloneGuiCore guiCore;


    public DefaultLoginListener(AbstractLoginWindow loginWindow, StandaloneGuiCore guiCore) {
        this.loginWindow = loginWindow;
        this.guiCore = guiCore;
    }


    public void loginOk(UserId userId, ConnectionPool pool, User user) {
        loginWindow.testNbConnections(pool);
        loginWindow.jukeBox.playSuccessSound();
        loginWindow.handleLogin(pool, user, guiCore);
    }


    public void loginFailed(final FailureCause cause, final Throwable error) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loginWindow.handleLoginFailed(cause, error);
            }
        });
        try {
            guiCore.stop();
        }
        catch (Exception e) {
            logger.warn("Arret du container JADE en erreur", e);
        }
    }
}
