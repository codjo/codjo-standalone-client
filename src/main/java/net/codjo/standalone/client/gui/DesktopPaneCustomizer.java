package net.codjo.standalone.client.gui;
import java.awt.Color;
import javax.swing.Icon;
/**
 *
 */
public interface DesktopPaneCustomizer {
    Color getColor(Environment environment);


    Icon getIcon(Environment environment);


    String getLabel(Environment environment);
}
