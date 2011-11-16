package net.codjo.standalone.client.gui;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import junit.framework.TestCase;
/**
 *
 */
public class DefaultDesktopPaneCustomizerTest extends TestCase {
    private DefaultDesktopPaneCustomizer customizer = new DefaultDesktopPaneCustomizer();


    public void test_defaultIcon() throws Exception {
        assertNull(customizer.getIcon(Environment.DEV));

        Icon icon = new ImageIcon();
        customizer.setDefaultIcon(icon);

        assertSame(icon, customizer.getIcon(Environment.DEV));
        assertSame(icon, customizer.getIcon(Environment.INT));
    }


    public void test_specificIcon() throws Exception {
        Icon defaultIcon = new ImageIcon();
        Icon devIcon = new ImageIcon();
        customizer.setDefaultIcon(defaultIcon);
        customizer.setIconFor(Environment.DEV, devIcon);

        assertSame(devIcon, customizer.getIcon(Environment.DEV));
        assertSame(defaultIcon, customizer.getIcon(Environment.INT));
    }


    public void test_defaultColor() throws Exception {
        assertNull(customizer.getColor(Environment.DEV));

        Color color = Color.BLUE;
        customizer.setDefaultColor(color);

        assertSame(color, customizer.getColor(Environment.DEV));
        assertSame(color, customizer.getColor(Environment.INT));
    }


    public void test_specificColor() throws Exception {
        customizer.setDefaultColor(Color.BLUE);
        customizer.setColorFor(Environment.DEV, Color.GRAY);

        assertSame(Color.GRAY, customizer.getColor(Environment.DEV));
        assertSame(Color.BLUE, customizer.getColor(Environment.INT));
    }


    public void test_defaultLabel() throws Exception {
        assertNull(customizer.getLabel(Environment.DEV));

        String label = "TEST";
        customizer.setDefaultLabel(label);

        assertSame(label, customizer.getLabel(Environment.DEV));
        assertSame(label, customizer.getLabel(Environment.INT));
    }


    public void test_specificLabel() throws Exception {
        String defaultLabel = "TEST";
        customizer.setDefaultLabel(defaultLabel);
        customizer.setLabelFor(Environment.DEV, "my dev");

        assertSame("my dev", customizer.getLabel(Environment.DEV));
        assertSame(defaultLabel, customizer.getLabel(Environment.INT));
    }
}
