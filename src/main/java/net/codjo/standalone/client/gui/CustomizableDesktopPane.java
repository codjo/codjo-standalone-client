package net.codjo.standalone.client.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
/**
 *
 */
public class CustomizableDesktopPane extends JDesktopPane {
    private Icon icon = null;
    private String environmentLabel = null;


    public void initialize(DesktopPaneCustomizer customizer, Environment environment) {
        this.environmentLabel = customizer.getLabel(environment);
        this.icon = customizer.getIcon(environment);

        Color color = customizer.getColor(environment);
        if (color != null) {
            setBackground(color);
        }

        setFont(new Font("Arial", Font.PLAIN, 20));
    }


    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        //noinspection LocalVariableNamingConvention
        int x, y = 0;
        int height = 0, width;
        Dimension windowSize = getSize();
        if (icon != null) {
            height = icon.getIconHeight();
            width = icon.getIconWidth();
            x = (windowSize.width / 2) - (width / 2);
            y = (windowSize.height / 2) - (height / 2);
            icon.paintIcon(this, graphics, x, y);
        }

        if (environmentLabel != null) {
            FontMetrics fontMetrics = getFontMetrics(getFont());
            int labelWidth = fontMetrics.stringWidth(environmentLabel);
            y = (y + height + 20);
            x = (windowSize.width - labelWidth) / 2;
            graphics.drawString(environmentLabel, x, y);
        }
    }
}