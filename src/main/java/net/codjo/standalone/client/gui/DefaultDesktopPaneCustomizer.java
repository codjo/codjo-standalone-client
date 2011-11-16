package net.codjo.standalone.client.gui;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
/**
 *
 */
public class DefaultDesktopPaneCustomizer implements DesktopPaneCustomizer {
    private Element<Icon> icon = new Element<Icon>();
    private Element<Color> color = new Element<Color>();
    private Element<String> label = new Element<String>();


    public Icon getIcon(Environment environment) {
        return icon.getValue(environment);
    }


    public void setDefaultIcon(Icon defaultIcon) {
        icon.setDefaultValue(defaultIcon);
    }


    public void setIconFor(Environment environment, Icon icon) {
        this.icon.setValueFor(environment, icon);
    }


    public Color getColor(Environment environment) {
        return color.getValue(environment);
    }


    public void setDefaultColor(Color defaultColor) {
        color.setDefaultValue(defaultColor);
    }


    public void setColorFor(Environment environment, Color color) {
        this.color.setValueFor(environment, color);
    }


    public String getLabel(Environment environment) {
        return label.getValue(environment);
    }


    public void setDefaultLabel(String label) {
        this.label.setDefaultValue(label);
    }


    public void setLabelFor(Environment environment, String label) {
        this.label.setValueFor(environment, label);
    }


    static class Element<T> {
        private T defaultValue;
        private Map<Environment, T> values = new HashMap<Environment, T>();


        public T getValue(Environment environment) {
            T value = values.get(environment);
            if (value == null) {
                return defaultValue;
            }
            return value;
        }


        public void setDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
        }


        public void setValueFor(Environment environment, T value) {
            values.put(environment, value);
        }
    }
}
