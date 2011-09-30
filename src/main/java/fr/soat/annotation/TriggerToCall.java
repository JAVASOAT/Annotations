package fr.soat.annotation;

import java.lang.reflect.Method;

/**
 * Informations sur une méthode abonnées pour pouvoir l'apellée via Reflection
 */
public class TriggerToCall {
    /**
     * La méthode abonnée
     */
    private Method method;
    /**
     * L'instance du bean contenant la méthode abonnée
     */
    private Object bean;

    public TriggerToCall(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }
}
