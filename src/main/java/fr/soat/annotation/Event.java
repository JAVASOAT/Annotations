package fr.soat.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Un évènement levé par le système
 */
public class Event {

    /**
     * Le type de l'évènement
     */
    private String type;
    /**
     * Les paramètres de l'évènement
     */
    private Map<String, Object> params = new HashMap<String, Object>();

    public Event(String type, Map<String, Object> params) {
        this.type = type;
        this.params = params;
    }

    public Event(String event) {
        this.type = event;
    }

    public String getType() {
        return type;
    }

    public Object getParam(String name) {
        return params.get(name);
    }
}