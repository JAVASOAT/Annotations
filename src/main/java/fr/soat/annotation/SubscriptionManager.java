package fr.soat.annotation;

public interface SubscriptionManager {
    /**
     * Dispatch un évènement à tous ses abonnés
     * @param event L'évènement à transmettre
     */
    void dispatchEvent(Event event);
}