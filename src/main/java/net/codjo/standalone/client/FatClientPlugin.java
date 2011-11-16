/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.standalone.client;
import net.codjo.agent.Agent;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.ServiceException;
import net.codjo.agent.UserId;
import net.codjo.agent.behaviour.OneShotBehaviour;
import net.codjo.agent.imtp.NoConnectionIMTPManager;
import net.codjo.crypto.common.StringEncrypter;
import net.codjo.gui.ApplicationData;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.plugin.common.ApplicationPlugin;
import net.codjo.plugin.common.session.SessionManager;
import net.codjo.security.common.AccountLockedException;
import net.codjo.security.common.BadLoginException;
import net.codjo.security.common.SecurityLevel;
import net.codjo.security.common.api.User;
import net.codjo.security.gui.user.UserFormAction;
import net.codjo.security.server.api.SecurityServiceHelper;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.JdbcServiceHelper;
import net.codjo.standalone.client.login.DatabaseData;
import net.codjo.standalone.client.login.LoginData;
import net.codjo.standalone.client.login.LoginListener;
import org.apache.log4j.Logger;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class FatClientPlugin implements ApplicationPlugin {
    private static final Logger LOG = Logger.getLogger(FatClientPlugin.class);
    private final ApplicationCore applicationCore;
    private final MutablePicoContainer picoContainer;
    private final LoginListener loginListener;
    private final ApplicationData applicationData;
    private final SessionManager sessionManager;
    private final Thread shutdownHook = new CleanupThread();
    private boolean shutdownInProgress = false;


    public FatClientPlugin(ApplicationCore applicationCore, MutablePicoContainer picoContainer,
                           LoginListener loginListener, ApplicationData applicationData,
                           SessionManager sessionManager) {
        this.applicationCore = applicationCore;
        this.picoContainer = picoContainer;
        this.loginListener = loginListener;
        this.applicationData = applicationData;
        this.sessionManager = sessionManager;
    }


    public void initContainer(ContainerConfiguration configuration) throws Exception {
        configuration.setJadeFileDirToTemporaryDir();
        configuration.setParameter("platform-id",
                                   applicationData.getName() + "-" + applicationData.getVersion());

        configuration.setParameter("mtps", null);
        configuration.setParameter("imtp", NoConnectionIMTPManager.class.getName());

        DatabaseData databaseData = (DatabaseData)picoContainer.getComponentInstance(DatabaseData.class);
        configuration.setParameter("JDBCService.driver", applicationData.getDriver());
        configuration.setParameter("JDBCService.url", databaseData.getUrl());
        configuration.setParameter("JDBCService.catalog", databaseData.getCatalog());

        configuration.setParameter("LdapSecurityService.jdbc.login", decrypt(databaseData.getLogin()));
        configuration.setParameter("LdapSecurityService.jdbc.password", decrypt(databaseData.getPassword()));

        configuration.setParameter("SecurityService.config",
                                   applicationData.getData().getProperty("SecurityService.config"));
    }


    private String decrypt(String value) {
        return new StringEncrypter(Constants.KEY).decrypt(value);
    }


    public void start(AgentContainer agentContainer) throws Exception {
        agentContainer.acceptNewAgent("login-agent", new LoginAgent()).start();
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }


    public void stop() throws Exception {
        if (!shutdownInProgress) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
    }


    public void stopSession() {
        sessionManager.stopSession(applicationCore.getGlobalComponent(UserId.class));
    }


    private class LoginAgent extends Agent {
        @Override
        protected void setup() {
            addBehaviour(new LoginBehaviour());
        }


        @Override
        protected void tearDown() {
        }
    }

    private class LoginBehaviour extends OneShotBehaviour {
        @Override
        protected void action() {
            try {
                LoginData loginData = (LoginData)picoContainer.getComponentInstance(LoginData.class);
                UserId userId = getSecurityService().login(loginData.getLogin(), loginData.getPassword(),
                                                           SecurityLevel.USER);
                sessionManager.startSession(userId);
                final ConnectionPool pool = getJdbcService().getPool(userId);
                User user = getSecurityService().getUser(userId);
                applicationCore.addGlobalComponent(User.class, user);
                applicationCore.addGlobalComponent(userId);

                picoContainer.registerComponentImplementation(UserFormAction.class);

                if (!user.isAllowedTo("use-application")) {
                    loginListener.loginFailed(LoginListener.FailureCause.GRANT_ERROR, null);
                    return;
                }
                loginListener.loginOk(userId, pool, user);
            }
            catch (ServiceException e) {
                loginListener.loginFailed(LoginListener.FailureCause.SERVICE_ERROR, e);
            }
            catch (BadLoginException e) {
                loginListener.loginFailed(LoginListener.FailureCause.BAD_LOGIN, e);
            }
            catch (AccountLockedException e) {
                loginListener.loginFailed(LoginListener.FailureCause.ACCOUNT_LOCKED, e);
            }
            catch (Throwable e) {
                loginListener.loginFailed(LoginListener.FailureCause.TECHNICAL_ERROR, e);
            }
            getAgent().die();
        }


        private JdbcServiceHelper getJdbcService()
              throws ServiceException {
            return ((JdbcServiceHelper)getAgent().getHelper(JdbcServiceHelper.NAME));
        }


        private SecurityServiceHelper getSecurityService()
              throws ServiceException {
            return ((SecurityServiceHelper)getAgent().getHelper(SecurityServiceHelper.NAME));
        }
    }
    private class CleanupThread extends Thread {

        @Override
        public void run() {
            try {
                shutdownInProgress = true;
                stopSession();
                FatClientPlugin.this.applicationCore.stop();
            }
            catch (Exception e) {
                LOG.warn("Impossible de fermer correctement l'application", e);
            }
        }
    }
}
