package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.net.InetAddress;

import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.provision.ResourceScanner;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of ResourceScanner interface.
 * <p>Basically, it won't do anything.</p>
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class DefaultResourceScanner implements ResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultResourceScanner.class);

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.ResourceScanner#scanResource(org.opennms.netmgt.model.OnmsResource, java.io.File, java.net.InetAddress)
     */
    public void scanResource(OnmsResource resource, File resourceDir, InetAddress nodeIpAddress) throws ResourceScannerException {
        LOG.debug("the resource {} does not have a custom scanner; all properties remain untouched.", resource.toString());
    }

}
