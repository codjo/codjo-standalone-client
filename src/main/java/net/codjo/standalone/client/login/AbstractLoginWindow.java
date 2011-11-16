/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.standalone.client.login;
import net.codjo.gui.ApplicationData;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.security.common.AccountLockedException;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.standalone.client.StandaloneGuiCore;
import net.codjo.standalone.client.gui.Environment;
import net.codjo.utils.GuiUtil;
import net.codjo.utils.JukeBox;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;
/**
 *
 */
public abstract class AbstractLoginWindow extends javax.swing.JFrame {
    private static final Logger APP = Logger.getLogger(AbstractLoginWindow.class);
    protected JTextField loginField = new javax.swing.JTextField();
    protected JPasswordField passwordField = new javax.swing.JPasswordField();
    protected JComboBox serverCombo = new JComboBox();
    private BorderLayout borderLayout1 = new BorderLayout();
    private FlowLayout flowLayout1 = new FlowLayout();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel imageLabel = new JLabel();
    private JPanel jPanel2 = new JPanel();
    private JPanel jPanel1 = new JPanel();
    private JLabel loginLabel = new javax.swing.JLabel();
    private JButton okButton = new javax.swing.JButton();
    private JLabel passwordLabel = new javax.swing.JLabel();
    private JButton quitButton = new javax.swing.JButton();
    private String[][] servers;
    private String applicationName;
    JukeBox jukeBox;
    private final ApplicationData applicationData;
    private final StandaloneGuiCore standaloneGuiCore;
    private JCheckBox showPasswordCheckbox = new JCheckBox("Voir le mot de passe");


    /**
     * Constructor a partir d'une <code>ApplicationData</code>.
     *
     * @param jukeBox pour la zic
     */
    protected AbstractLoginWindow(ApplicationData application,
                                  JukeBox jukeBox,
                                  StandaloneGuiCore standaloneGuiCore) {
        applicationData = application;
        this.standaloneGuiCore = standaloneGuiCore;

        this.servers = new String[application.getServers().length][5];
        for (int i = 0; i < application.getServers().length; i++) {
            servers[i][0] = application.getServers()[i].getName();
            servers[i][1] = application.getServers()[i].getUrl();
            servers[i][2] = application.getServers()[i].getCatalog();

            if (Environment.REC.getLabel().equals(application.getServers()[i].getName().toUpperCase())) {
                servers[i][3] = getConfiguration("server.encrypted.login.recette");
                servers[i][4] = getConfiguration("server.encrypted.password.recette");
            }
            else if (Environment.PRD.getLabel().equals(application.getServers()[i].getName().toUpperCase())) {
                servers[i][3] = getConfiguration("server.encrypted.login.production");
                servers[i][4] = getConfiguration("server.encrypted.password.production");
            }
            else if (Environment.INT.getLabel().equals(application.getServers()[i].getName())) {
                servers[i][3] = getConfiguration("server.encrypted.login.integration");
                servers[i][4] = getConfiguration("server.encrypted.password.integration");
            }
            else {
                servers[i][3] = getConfiguration("server.encrypted.login.developpement");
                servers[i][4] = getConfiguration("server.encrypted.password.developpement");
            }
        }

        this.applicationName = application.getName() + " v-" + application.getVersion();
        this.jukeBox = jukeBox;
        imageLabel.setIcon(application.getIcon());
        jbInit();

        for (String[] server : servers) {
            serverCombo.addItem(server[0]);
        }
        serverCombo.setSelectedIndex(0);

        if (servers.length > 1) {
            loginField.setText(application.getDefaultLogin());
            passwordField.setText(application.getDefaultPassword());
        }
        else {
            loginField.setText(System.getProperty("user.name"));
        }
        setNameForGuiTest();
    }


    private void setNameForGuiTest() {
        loginField.setName("loginField");
        passwordField.setName("passwordField");
        serverCombo.setName("serverCombo");
    }


    /**
     * Call-Back appelle lorsque la connection a put etre faite avec le serveur.
     *
     * @param cm Un ConnectionPool valide
     */
    public abstract void handleLogin(ConnectionPool cm, User user, StandaloneGuiCore guiCore);


    /**
     * Description of the Method
     */
    public abstract void requestQuit();


    void passwordFieldFocusGained(FocusEvent event) {
        passwordField.selectAll();
    }


    private boolean isEnvironnement(String env) {
        String environnement = servers[serverCombo.getSelectedIndex()][0];
        return env.equalsIgnoreCase(environnement);
    }


    /**
     * Init Gui
     */
    private void jbInit() {
        this.getContentPane().setLayout(borderLayout1);
        jPanel1.setBorder(BorderFactory.createEtchedBorder());
        jPanel1.setLayout(gridBagLayout1);
        passwordLabel.setText("Mot de passe Windows");
        loginLabel.setText("Compte Windows");
        okButton.setText("OK");
        okButton.setActionCommand("OK");
        okButton.setEnabled(false);
        quitButton.setText("Quitter");
        quitButton.setActionCommand("Quitter");
        //noinspection deprecation
        loginField.setNextFocusableComponent(passwordField);
        //noinspection deprecation
        passwordField.setNextFocusableComponent(okButton);
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                passwordFieldFocusGained(event);
            }
        });

        DocumentListener listener = new ButtonEnabilityListener(loginField, passwordField, okButton);
        loginField.getDocument().addDocumentListener(listener);
        passwordField.getDocument().addDocumentListener(listener);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel2.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.RIGHT);
        this.getContentPane().add(imageLabel, BorderLayout.CENTER);
        this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(loginField,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL, new Insets(15, 20, 0, 20), 0, 0));
        jPanel1.add(loginLabel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(15, 20, 0, 0), 0, 0));
        jPanel1.add(passwordLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(15, 20, 0, 0), 0, 0));
        jPanel1.add(passwordField,
                    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL, new Insets(15, 20, 0, 20), 0, 0));
        jPanel1.add(showPasswordCheckbox,
                    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL, new Insets(15, 20, 0, 20), 0, 0));
        jPanel1.add(serverCombo,
                    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                           GridBagConstraints.HORIZONTAL, new Insets(15, 20, 0, 20), 0, 0));
        jPanel1.add(jPanel2,
                    new GridBagConstraints(0, 4, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                                           GridBagConstraints.HORIZONTAL, new Insets(10, 0, 5, 15), 0, 0));
        jPanel2.add(okButton, null);
        jPanel2.add(quitButton, null);

        setTitle("Connexion à " + applicationName);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        pack();
        GuiUtil.centerWindow(this);
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                quitButtonActionPerformed();
            }
        });
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent event) {
                okButtonActionPerformed();
            }
        });
        showPasswordCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                char echoChar;
                if (showPasswordCheckbox.isSelected()) {
                    echoChar = 0;
                }
                else {
                    echoChar = '*';
                }
                passwordField.setEchoChar(echoChar);
            }
        });

        okButton.setMnemonic(KeyEvent.VK_O);
        quitButton.setMnemonic(KeyEvent.VK_Q);

        getRootPane()

              .

                    setDefaultButton(okButton);
    }


    public StandaloneGuiCore getStandaloneGuiCore() {
        return standaloneGuiCore;
    }


    private void okButtonActionPerformed() {
        okButton.setEnabled(false);
        try {
            String[] currentServer = servers[serverCombo.getSelectedIndex()];
            standaloneGuiCore.tryToStart(loginField.getText(),
                                         new String(passwordField.getPassword()),
                                         currentServer[1],
                                         currentServer[2],
                                         currentServer[3],
                                         currentServer[4]);
        }
        catch (Throwable ex) {
            handleLoginFailed(LoginListener.FailureCause.TECHNICAL_ERROR, ex);
        }
    }


    void testNbConnections(ConnectionPool cm) {
        Connection con = null;
        CallableStatement cstmt = null;
        String userName = loginField.getText().toUpperCase();
        String appName = applicationData.getName();
        appName = appName.substring(0, Math.min(appName.length(), 15)) + "%";
        String catalog = servers[serverCombo.getSelectedIndex()][2];
        int nbConnections = 0;

        try {
            con = cm.getConnection();
            cstmt = con.prepareCall(
                  "{call sp_COMMON_Nb_Connexion @USER_NAME=?, @APPLICATION_NAME=?, @CATALOG_NAME=?}");
            cstmt.setString(1, userName);
            cstmt.setString(2, appName);
            cstmt.setString(3, catalog);

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                nbConnections = rs.getInt(1);
            }
            if ((isEnvironnement("production") || isEnvironnement("recette")) && nbConnections > 1) {
                ErrorDialog.show(this, "Utilisateur déjà connecté",
                                 "L'identifiant " + userName + " est déjà connecté à l'application "
                                 + applicationName
                                 + ".\nImpossible de continuer.");
                System.exit(-1);
            }
        }
        catch (Exception ex) {
            ErrorDialog.show(this, "Erreur", ex);
            System.exit(-1);
        }
        finally {
            try {
                cm.releaseConnection(con, cstmt);
            }
            catch (SQLException ex) {
                APP.error(ex);
            }
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @noinspection UNUSED_SYMBOL
     */
    private void quitButtonActionPerformed() {
        requestQuit();
    }


    public void handleLoginFailed(LoginListener.FailureCause cause, Throwable error) {
        passwordField.setText("");
        repaint();
        jukeBox.playFailureSound();
        if (cause == LoginListener.FailureCause.GRANT_ERROR) {
            APP.error("L'utilisateur " + loginField.getText()
                      + " n'est pas autorisé à utiliser cette application");
            ErrorDialog.show(this, "Erreur d'autorisation",
                             "Vous n'avez pas les droits d'accès à cette application.\n\n Veuillez contacter votre administrateur.");
        }
        else {
            String errorMessage = error.getLocalizedMessage();

            if (cause == LoginListener.FailureCause.SERVICE_ERROR
                || cause == LoginListener.FailureCause.TECHNICAL_ERROR) {
                APP.error("Erreur de login : " + error.getMessage(), error);
            }
            else if (cause == LoginListener.FailureCause.ACCOUNT_LOCKED) {
                APP.error("Compte utilisateur bloqué : " + error.getMessage(), error);
                errorMessage = ((AccountLockedException)error).getUrlToUnlock();
            }
            else {
                APP.warn(cause);
            }
            ErrorDialog.show(this, "Erreur de Login",
                             "La connexion a échoué (" + cause + ") : " + errorMessage, error);
        }
    }


    protected String getConfiguration(String key) {
        return applicationData.getData().getProperty(key);
    }


    private static class ButtonEnabilityListener implements DocumentListener {
        private final JTextField loginField;
        private final JPasswordField passwordField;
        private final JButton okButton;


        ButtonEnabilityListener(JTextField loginField, JPasswordField passwordField, JButton okButton) {
            this.loginField = loginField;
            this.passwordField = passwordField;
            this.okButton = okButton;
        }


        public void changedUpdate(DocumentEvent event) {
            okButton.setEnabled(loginField.getText().length() > 0 && passwordField.getPassword().length > 0);
        }


        public void insertUpdate(DocumentEvent event) {
            changedUpdate(event);
        }


        public void removeUpdate(DocumentEvent event) {
            changedUpdate(event);
        }
    }
}
