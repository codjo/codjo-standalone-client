package net.codjo.standalone.client.login;
import net.codjo.gui.ApplicationData;
import net.codjo.gui.SplashScreen;
import net.codjo.gui.toolkit.swing.SwingWorker;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.standalone.client.StandaloneGuiCore;
import net.codjo.standalone.client.action.QuitAction;
import net.codjo.standalone.client.gui.Environment;
import net.codjo.utils.JukeBox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
/**
 *
 */
public class DefaultLoginWindow extends AbstractLoginWindow {
    private final QuitAction quitAction;
    private final Map<String, String> serverNameMap = new HashMap<String, String>();


    public DefaultLoginWindow(ApplicationData application,
                              JukeBox jukeBox,
                              StandaloneGuiCore standaloneGuiCore,
                              QuitAction quitAction) {
        super(application, jukeBox, standaloneGuiCore);
        this.quitAction = quitAction;

        if (Environment.REC.getLabel().equals(serverCombo.getSelectedItem().toString().toUpperCase())) {
            loginField.setText(System.getProperty("user.name"));
        }

        //remplissage de la Map de traduction des noms de serveurs
        serverCombo.addItemListener(new ServerComboListener());

        serverNameMap.put(Environment.REC.getLabel(), getConfiguration("server.base.recette"));
        serverNameMap.put(Environment.PRD.getLabel(), getConfiguration("server.base.production"));
        serverNameMap.put(Environment.DEV.getLabel(), getConfiguration("server.base.developpement"));
        serverNameMap.put(Environment.INT.getLabel(), getConfiguration("server.base.integration"));
    }


    @Override
    public void handleLogin(final ConnectionPool connectionPool, User user, StandaloneGuiCore guiCore) {
        //noinspection SuspiciousMethodCalls
        final String serverBase = serverNameMap.get(serverCombo.getSelectedItem());
        final String serverName = (String)serverCombo.getSelectedItem();

        System.setProperty("user.environment", serverName);
        setVisible(false);
        new SplashScreen(new ImageIcon(getClass().getResource("/images/splash.gif")), null, 5000);
        SwingWorker sw =
              new SwingWorker() {
                  @Override
                  public Object construct() {
                      try {
                          getStandaloneGuiCore().initGui(connectionPool);
                      }
                      catch (Exception e) {
                          manageFailure(e);
                      }
                      return null;
                  }


                  @Override
                  public void finished() {
                      dispose();
                      try {
                          getStandaloneGuiCore().displayGui(serverBase, serverName);
                      }
                      catch (Exception e) {
                          manageFailure(e);
                      }
                  }


                  private void manageFailure(Exception error) {
                      Logger.getLogger(DefaultLoginWindow.class).error(error);
                      ErrorDialog.show(DefaultLoginWindow.this, "Erreur de connexion avec la Base : ",
                                       error.getLocalizedMessage());
                      requestQuit();
                  }
              };
        sw.start();
    }


    @Override
    public void requestQuit() {
        quitAction.doAction();
    }


    private class ServerComboListener implements ItemListener {
        public void itemStateChanged(ItemEvent event) {
            if (Environment.DEV.getLabel().equals(serverCombo.getSelectedItem().toString())) {
                loginField.setText("user_dev");
            }
            else if (Environment.INT.getLabel().equals(serverCombo.getSelectedItem().toString())) {
                loginField.setText("user_tr");
            }
            else if (Environment.REC
                  .getLabel()
                  .equals(serverCombo.getSelectedItem().toString().toUpperCase())) {
                loginField.setText(System.getProperty("user.name"));
            }
            else {
                loginField.setText("");
            }
            if (Environment.REC.getLabel().equals(serverCombo.getSelectedItem().toString().toUpperCase())
                || Environment.PRD.getLabel()
                  .equals(serverCombo.getSelectedItem().toString().toUpperCase())) {
                passwordField.setText("");
            }
            else {
                passwordField.setText(loginField.getText());
            }
        }
    }
}
