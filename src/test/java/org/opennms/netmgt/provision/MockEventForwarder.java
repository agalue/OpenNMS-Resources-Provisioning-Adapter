package org.opennms.netmgt.provision;

import java.util.ArrayList;
import java.util.List;

import org.opennms.netmgt.model.events.EventForwarder;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Log;

/**
 * The Class MockEventForwarder.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class MockEventForwarder implements EventForwarder {

    /** The events. */
    private List<Event> events = new ArrayList<Event>();

    /* (non-Javadoc)
     * @see org.opennms.netmgt.model.events.EventForwarder#sendNow(org.opennms.netmgt.xml.event.Event)
     */
    public void sendNow(Event event) {
        events.add(event);
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.model.events.EventForwarder#sendNow(org.opennms.netmgt.xml.event.Log)
     */
    public void sendNow(Log eventLog) {
        events.addAll(eventLog.getEvents().getEventCollection());
    }

    /**
     * Gets the events.
     *
     * @return the events
     */
    public List<Event> getEvents() {
        return events;
    }

}
