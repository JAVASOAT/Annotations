package fr.soat.annotation;

import fr.soat.annotation.annotations.EventParam;
import fr.soat.annotation.annotations.EventSubscriber;
import fr.soat.annotation.annotations.Trigger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Gestion des abonnements via Spring
 */
@Service
public class SpringSubscriptionManager implements SubscriptionManager, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<String, List<TriggerToCall>> triggers = new HashMap<String, List<TriggerToCall>>();

    /**
     * Scan le classpath pour trouver les méthodes abonnées lors de l'instanciation
     */
    @PostConstruct
    public void scanSubscribers() {
        // initialisation du scanner de classpath
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        AnnotationTypeFilter filter = new AnnotationTypeFilter(EventSubscriber.class);

        scanner.addIncludeFilter(filter);

        // scan du classpath
        Set<BeanDefinition> beans = scanner.findCandidateComponents("fr.soat.annotation");

        for (BeanDefinition beanDefinition : beans) {
            Class beanClass;
            try {
                // recherche des methodes abonnées
                beanClass = Class.forName(beanDefinition.getBeanClassName());
                for (Method curMethod : beanClass.getMethods()) {
                    Trigger annotation = curMethod.getAnnotation(Trigger.class);
                    if (annotation != null) {
                        String eventName = annotation.value();
                        Object bean = applicationContext.getBean(beanClass);
                        registerListener(eventName, curMethod, bean);
                    }
                }

            } catch (ClassNotFoundException e) {
                // spring a indexé une classe qui n'existe pas..
                throw new RuntimeException("Configuration fatal error!!!", e);
            }
        }
    }

    /**
     * Ajoute une méthode abonnée à la liste
     * @param eventName Nom de l'évènement
     * @param method Méthode abonnée
     * @param bean Instance de la classe contenant la méthode
     */
    public void registerListener(String eventName, Method method, Object bean) {
        List<TriggerToCall> triggersToCall = triggers.get(eventName);
        if (triggersToCall == null) {
            triggersToCall = new ArrayList<TriggerToCall>();
        }
        triggersToCall.add(new TriggerToCall(method, bean));
        triggers.put(eventName, triggersToCall);
    }

    public void dispatchEvent(Event event) {
        Collection<TriggerToCall> eventTriggers = triggers.get(event.getType());

        if (eventTriggers != null) {
            for (TriggerToCall trigger : eventTriggers) {
                try {
                    trigger.getMethod().invoke(trigger.getBean(), extractMethodParameters(trigger, event));
                } catch (IllegalAccessException e) {
                    e.printStackTrace(); // change with your logging system
                } catch (InvocationTargetException e) {
                    e.printStackTrace(); // change with your logging system
                }
            }
        }

    }

    /**
     * Extraction des paramètre à transmettre à la méthode abonnée
     * @param trigger Les informations sur la méthode abonnée
     * @param event L'évènement
     * @return Un tableau contenant tous les paramètres à transmettre à la méthode.
     */
    private Object[] extractMethodParameters(TriggerToCall trigger, Event event) {
        // extraction des types des paramètres
        Class[] types = trigger.getMethod().getParameterTypes();

        // pas de paramètres!
        if (types.length == 0) {
            return new Object[0];
        }

        // le paramètre est l'évènement
        else if (types.length == 1 && types[0] == Event.class) {
            return new Object[]{event};
        }

        // introspection pour récupérer les annotations
        List<EventParam> paramList = new ArrayList<EventParam>();
        Annotation[][] annotationTypes = trigger.getMethod().getParameterAnnotations();
        for (Annotation[] annotations : annotationTypes) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof EventParam) {
                    paramList.add((EventParam) annotation);
                }
            }
        }

        // check que tous les paramètres sont bien annotés
        if (paramList.size() != types.length) {
            throw new RuntimeException("You need to annotate all the parameters of your trigger");
        }

        List<Object> params = new ArrayList<Object>();

        // Création de la liste de paramètres
        int curParam = 0;
        for (EventParam param : paramList) {
            Object value = event.getParam(param.value());
            // le nom et le type matches
            if (value != null && value.getClass().isAssignableFrom(types[curParam])) {
                params.add(value);
            }
            // le type ne match pas
            else if (value != null) {
                throw new RuntimeException("Invalid type  : '" + value.getClass().getName() + "' expected '" + types[curParam].getName());
            }
            // paramètre inconnu
            else {
                throw new RuntimeException("Parameter not found : '" + param.value() + "'");
            }
            curParam++;
        }

        return params.toArray();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
