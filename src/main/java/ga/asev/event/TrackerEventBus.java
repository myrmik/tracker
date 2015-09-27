package ga.asev.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.vaadin.spring.annotation.SpringComponent;

/**
 * A simple wrapper for Guava event bus. Defines static convenience methods for
 * relevant actions.
 */
@SpringComponent
public class TrackerEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);

    public void post(final Object event) {
        eventBus.post(event);
    }

    public void register(final Object object) {
        eventBus.register(object);
    }

    public void unregister(final Object object) {
        eventBus.unregister(object);
    }

    @Override
    public final void handleException(final Throwable exception,
            final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
