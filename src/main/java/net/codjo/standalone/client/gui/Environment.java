package net.codjo.standalone.client.gui;
/**
 *
 */
public enum Environment {
    REC("RECETTE"),
    PRD("PRODUCTION"),
    DEV("Développement"),
    INT("Intégration");
    private String label;


    Environment(String label) {
        this.label = label;
    }


    public String getLabel() {
        return label;
    }


    public static Environment toEnum(String label) {
        for (Environment environment : Environment.values()) {
            if (label.toUpperCase().equals(environment.getLabel().toUpperCase())) {
                return environment;
            }
        }
        throw new IllegalArgumentException("Pas de correspondance pour '" + label + "'");
    }
}
