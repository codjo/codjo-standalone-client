/*
 *  Project: ALIS
 *
 *  Common Apache License 2.0
 */
package net.codjo.standalone.client.action;

//Imports java
import net.codjo.standalone.client.StandaloneGuiCore;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * Action de quittage de l'application
 */
public class QuitAction extends javax.swing.AbstractAction {
    private static final Logger LOG = Logger.getLogger(QuitAction.class);
    private final StandaloneGuiCore standaloneGuiCore;
    private JFrame parentFrame;


    public QuitAction(StandaloneGuiCore standaloneGuiCore) {
        this.standaloneGuiCore = standaloneGuiCore;
        putValue(NAME, "Quitter");
        putValue(SHORT_DESCRIPTION, "Quitter");
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("QuitAction.png")));
    }


    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }


    public void actionPerformed(ActionEvent event) {
        doAction();
    }


    public void doAction() {
        int response = 0;
        if (parentFrame != null) {
            response = JOptionPane.showConfirmDialog(parentFrame,
                                                     "Voulez-vous vraiment quitter l'application ?",
                                                     "Confirmation",
                                                     JOptionPane.YES_NO_OPTION);
        }
        if (response != 1) {
            try {
                standaloneGuiCore.stop();
            }
            catch (Throwable e) {
                LOG.error("Erreur lors du stop", e);
            }
            System.exit(-1);
        }
    }
}
