import fr.soat.annotation.Event;
import fr.soat.annotation.SpringSubscriptionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple test pour dispatcher des évènements.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class TestSubscription {

    @Autowired
    SpringSubscriptionManager mgr;

    @Test
    public void testSubscriptionIsOk() {

        mgr.dispatchEvent(new Event("event1"));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("param1", "value1");
        params.put("param2", 2);
        mgr.dispatchEvent(new Event("event2", params));
    }
}
