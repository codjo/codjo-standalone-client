package net.codjo.standalone.client;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.gui.ApplicationData;
import net.codjo.plugin.common.ApplicationPlugin;
import net.codjo.plugin.common.CommandLineArguments;
import net.codjo.plugin.gui.GuiCore;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.standalone.client.action.QuitAction;
import net.codjo.standalone.client.gui.CustomizableDesktopPane;
import net.codjo.standalone.client.gui.DefaultDesktopPaneCustomizer;
import net.codjo.standalone.client.gui.DesktopPaneCustomizer;
import net.codjo.standalone.client.gui.Environment;
import net.codjo.standalone.client.login.AbstractLoginWindow;
import net.codjo.standalone.client.login.DatabaseData;
import net.codjo.standalone.client.login.DefaultLoginListener;
import net.codjo.standalone.client.login.DefaultLoginWindow;
import net.codjo.standalone.client.login.LoginData;
import net.codjo.standalone.client.login.LoginListener;
import net.codjo.utils.JukeBox;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import java.awt.Color;
import java.io.IOException;
import javax.swing.JDesktopPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
/**
 *
 */
public class StandaloneGuiCore extends GuiCore {
    private AbstractLoginWindow loginWindow;
    private StandaloneClientConfiguration configuration = new StandaloneClientConfiguration();
    private CustomizableDesktopPane desktopPane;


    public StandaloneGuiCore(ApplicationData applicationData, JukeBox jukeBox) throws IOException {

        configureLookAndFeel();

        addGlobalComponent(applicationData);
        desktopPane = new CustomizableDesktopPane();
        addGlobalComponent(JDesktopPane.class, desktopPane);

        QuitAction quitAction = new QuitAction(this);
        addGlobalComponent(quitAction);

        loginWindow = new DefaultLoginWindow(applicationData, jukeBox, this, quitAction);
        addGlobalComponent(LoginListener.class, new DefaultLoginListener(loginWindow, this));

        addPlugin(FatClientPlugin.class);
    }


    @Deprecated
    public void show() {
        loginWindow.setVisible(true);
    }


    @Override
    public void show(String[] arguments) {
        if (arguments.length >= 6) {
            String login = arguments[0];
            String password = arguments[1];
            String dbUrl = arguments[2];
            String dbCatalog = arguments[3];
            String dbLogin = arguments[4];
            String dbPassword = arguments[5];
            getLogger().info("Connexion en mode autologin");
            try {
                tryToStart(login, password, dbUrl, dbCatalog, dbLogin, dbPassword);
            }
            catch (Exception e) {
                getLogger().error("Demarrage en erreur", e);
                throw new RuntimeException("Demarrage en erreur (compatbilite ascendante)", e);
            }
        }
        else {
            getLogger().info("Connexion en mode standard");
            loginWindow.setVisible(true);
        }
    }


    public void tryToStart(String login,
                           String password,
                           String dbUrl,
                           String dbCatalog, String dbLogin, String dbPassword) throws Exception {
        getLogger().info("Configuration de connexion : "
                         + "\n\tlogin = " + login
                         + "\n\tpassword = " + password
                         + "\n\tdbUrl = " + dbUrl
                         + "\n\tdbCatalog = " + dbCatalog
                         + "\n\tdbLogin = " + dbLogin
                         + "\n\tdbPassword = " + dbPassword);

        removeGlobalComponent(LoginData.class);
        addGlobalComponent(new LoginData(login.toUpperCase(), password));

        removeGlobalComponent(DatabaseData.class);
        addGlobalComponent(new DatabaseData(dbUrl, dbCatalog, dbLogin, dbPassword));

        start(new CommandLineArguments(new String[0]));
    }


    public StandaloneClientConfiguration getConfiguration() {
        return configuration;
    }


    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        }
        catch (UnsupportedLookAndFeelException e) {
            Logger.getLogger(StandaloneGuiCore.class)
                  .error("Erreur lors de l'initialisation de JGoodies.", e);
        }
        // Ncessaire lorsque un Editor Combo se trouve sur une table, cf :
        //   net.codjo.gabi.gui.referential.GroupDetailGui
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.focusCellBackground", new JTable().getSelectionBackground());
        UIManager.put("Table.focusCellForeground", Color.WHITE);
    }


    @Override
    protected AgentContainer createAgentContainer(ContainerConfiguration containerConfiguration) {
        return AgentContainer.createMainContainer(containerConfiguration);
    }


    public void initGui(ConnectionPool connectionPool) throws Exception {
        for (ApplicationPlugin plugin : getPlugins()) {
            if (plugin instanceof StandalonePlugin) {
                ((StandalonePlugin)plugin).initGui(connectionPool);
            }
        }
    }


    public void displayGui(String serverBase, String serverName) throws Exception {
        Environment environment = Environment.toEnum(serverName);
        desktopPane.initialize(getConfiguration().getDesktopPaneCustomizer(), environment);

        for (ApplicationPlugin plugin : getPlugins()) {
            if (plugin instanceof StandalonePlugin) {
                ((StandalonePlugin)plugin).displayGui(serverBase, serverName);
            }
        }
    }


    public static class StandaloneClientConfiguration {
        private DesktopPaneCustomizer desktopPaneCustomizer = new DefaultDesktopPaneCustomizer();


        public DesktopPaneCustomizer getDesktopPaneCustomizer() {
            return desktopPaneCustomizer;
        }


        public void setDesktopPaneCustomizer(DesktopPaneCustomizer desktopPaneCustomizer) {
            this.desktopPaneCustomizer = desktopPaneCustomizer;
        }
    }
}
