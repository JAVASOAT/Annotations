package fr.soat.annotation;

import fr.soat.annotation.annotations.EventParam;
import fr.soat.annotation.annotations.EventSubscriber;
import fr.soat.annotation.annotations.Trigger;
import org.springframework.stereotype.Service;

/**
 * Classe contenant les abonnements
 */
@EventSubscriber
@Service
public class AnnotationEventSubscriber {

    @Trigger("event1")
    public void myTrigger() {
        System.out.println("Event 1 received!");
    }


    @Trigger("event2")
    public void anotherTrigger(@EventParam("param1") String param1, @EventParam("param2") Integer param2) {
        System.out.println("Event 2 received with param1 : " + param1 + " and param2:" + param2.toString());
    }

}
