/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.standalone.client;
import net.codjo.crypto.gui.CryptWindow;
import com.jgoodies.looks.FontSizeHints;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JFrame;
/**
 * Fenetre d'encodage destiné au DBA.
 */
public class DbaWindow extends JFrame {
    private JButton quitButton = new JButton("Quitter");


    public DbaWindow() {
        super("Cryptage et décryptage");
        addListeners();
        initGui();
    }


    private void initGui() {
        JPanel mainPanel = new CryptWindow().getMainPanel();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(quitButton);

        getContentPane().add(mainPanel, BorderLayout.NORTH);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }


    private void addListeners() {
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                quitButtonActionPerformed();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                requestQuit();
            }
        });
    }


    private static void configureLookAndFeel() {
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
        Plastic3DLookAndFeel.setMyCurrentTheme(new ExperienceBlue());
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        }
        catch (UnsupportedLookAndFeelException e) {
            ;
        }
    }


    private void quitButtonActionPerformed() {
        requestQuit();
    }


    public void requestQuit() {
        dispose();
        System.exit(0);
    }


    public static void main(String[] args) {
        configureLookAndFeel();
        new DbaWindow();
    }
}
