package org.opennms.netmgt.provision.resourcescanners;

import java.net.InetAddress;

import org.opennms.netmgt.config.SnmpPeerFactory;
import org.opennms.netmgt.provision.ResourceScanner;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.opennms.netmgt.snmp.CollectionTracker;
import org.opennms.netmgt.snmp.SnmpAgentConfig;
import org.opennms.netmgt.snmp.SnmpUtils;
import org.opennms.netmgt.snmp.SnmpWalker;

/**
 * The abstract Class for SNMP Resource Scanners.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public abstract class AbstractSnmpResourceScanner implements ResourceScanner {

    /**
     * Creates the tracker.
     *
     * @param <C> the generic type
     * @param clazz the class of the SNMP Table tracker
     * @param address the address
     * @return the c
     * @throws ResourceScannerException the resource scanner exception
     */
    protected <C extends CollectionTracker> C createTracker(Class<C> clazz, InetAddress address) throws ResourceScannerException {
        SnmpWalker walker;
        C tracker;
        try {
            Class<?>[] params = { InetAddress.class };
            tracker = clazz.getConstructor(params).newInstance(address);

            SnmpAgentConfig agentConfig = SnmpPeerFactory.getInstance().getAgentConfig(address);
            walker = SnmpUtils.createWalker(agentConfig, tracker.getClass().getName(), tracker);
            walker.start();

            walker.waitFor();
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
        if (walker != null && walker.failed()) {
            throw new ResourceScannerException(walker.getErrorMessage());
        }
        return tracker;
    }
}
