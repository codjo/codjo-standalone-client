/*
 *  Project: ALIS
 *
 *  Common Apache License 2.0
 */
package net.codjo.standalone.client.action;

//Imports java
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.codjo.standalone.client.StandaloneGuiCore;
import org.apache.log4j.Logger;

/**
 * Action de quittage de l'application
 */
public class QuitAction extends javax.swing.AbstractAction {
    private static final Logger LOG = Logger.getLogger(QuitAction.class);
    private final StandaloneGuiCore standaloneGuiCore;
    private JFrame parentFrame;

    /** Une liste d'action à effectuer avant de fermer l'appliaction */
    private List<Runnable> quitTasks;

    public QuitAction(StandaloneGuiCore standaloneGuiCore) {
        this.standaloneGuiCore = standaloneGuiCore;
        quitTasks = new ArrayList<Runnable>();
        putValue(NAME, "Quitter");
        putValue(SHORT_DESCRIPTION, "Quitter");
        putValue(SMALL_ICON, new ImageIcon(getClass().getResource("QuitAction.png")));
    }

    public void addQuitTask(Runnable task) {
        quitTasks.add(task);
    }

    public void removeQuitTask(Runnable task) {
        quitTasks.remove(task);
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

            if(!quitTasks.isEmpty()) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                boolean allRight = true;

                List<Future<?>> tasks = new ArrayList<Future<?>>();
                for(Runnable closingTask : quitTasks) {
                    tasks.add(executor.submit(closingTask));
                }

                for(Future<?> future : tasks) {
                   try {
                       future.get();//Juste pour voir si il y a eu un problème
                   }
                   catch (Throwable tt) {
                       LOG.error(tt.getMessage(), tt);
                       allRight = false;
                  }
                }

                if(!allRight) {
                    JOptionPane.showMessageDialog(parentFrame,
                                  "Certaines tâches de fermeture ont échoué (voir les logs de l'application)",
                                  "Information",
                                  JOptionPane.WARNING_MESSAGE);
                }
            }

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
