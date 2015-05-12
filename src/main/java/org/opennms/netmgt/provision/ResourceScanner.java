package org.opennms.netmgt.provision;

import java.io.File;
import java.net.InetAddress;

import org.opennms.netmgt.model.OnmsResource;

/**
 * The Interface ResourceScanner.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public interface ResourceScanner {

    /**
     * Scan resource.
     *
     * @param resource the resource
     * @param resourceDir the resource directory (the location of RRD files).
     * @param nodeIpAddress the node IP address (to gather additional information).
     * 
     * @throws ResourceScannerException the resource scanner exception
     */
    void scanResource(OnmsResource resource, File resourceDir, InetAddress nodeIpAddress) throws ResourceScannerException;
}
