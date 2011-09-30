package fr.soat.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.PrivateKey;

/**
 * Annotation pour signaler les méthodes abonnées.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trigger {
    // type de l'évènement auquel la méthode s'abonne.
    String value();
}
