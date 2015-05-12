package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.net.InetAddress;

import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.provision.ResourceScannerSource;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.opennms.netmgt.provision.resourcescanners.trackers.EntityTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Resource Scanner for Cisco CPU.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@ResourceScannerSource("cpmCPUTotalIndex")
public class SnmpCiscoCpuScanner extends AbstractSnmpResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpCiscoCpuScanner.class);

    /** The Constant Cisco CPU Name. */
    public static final String PARM_CPU_NAME = "cpmCPUName";

    /** The Entity Tracker. */
    protected EntityTracker m_entityTracker;

    /**
     * Initialize.
     *
     * @param nodeIpAddress the node IP address
     * @throws ResourceScannerException the resource scanner exception
     */
    protected void initialize(InetAddress nodeIpAddress) throws ResourceScannerException {
        if (m_entityTracker == null) {
            m_entityTracker = createTracker(EntityTracker.class, nodeIpAddress);
        }
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.ResourceScanner#scanResource(org.opennms.netmgt.model.OnmsResource, java.io.File, java.net.InetAddress)
     */
    public void scanResource(OnmsResource resource, File resourceDir, InetAddress nodeIpAddress) throws ResourceScannerException {
        initialize(nodeIpAddress);
        LOG.debug("scanResource: processing resource {}", resource.getId());
        try {
            String entityName = m_entityTracker.getLabel(resource.getName());
            String entity = (entityName != null ? entityName.replaceAll("\"", "") : resource.getName());

            ResourceTypeUtils.updateStringProperty(resourceDir, entity, PARM_CPU_NAME);
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
    }

}
