package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.net.InetAddress;
import java.util.StringTokenizer;

import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.provision.ResourceScannerSource;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.opennms.netmgt.provision.resourcescanners.trackers.InterfaceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Resource Scanner for Cisco NBAR.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@ResourceScannerSource("ciscoNbarProtocolIndex")
public class SnmpCiscoNbarScanner extends AbstractSnmpResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpCiscoNbarScanner.class);

    /** The Constant Cisco NBAR Port Name. */
    public static final String PARM_NBAR_PORT = "cnpdStsPort";

    /** The Constant Cisco NBAR Port Alias. */
    public static final String PARM_NBAR_PORT_ALIAS = "cnpdStsPortAlias";

    /** The Constant Cisco NBAR Port ifIndex. */
    public static final String PARM_NBAR_PORT_INDEX = "cnpdStsPortIfIndex";

    /** The Interface Tracker. */
    protected InterfaceTracker m_interfaceTracker;

    /**
     * Initialize.
     *
     * @param nodeIpAddress the node IP address
     * @throws ResourceScannerException the resource scanner exception
     */
    protected void initialize(InetAddress nodeIpAddress) throws ResourceScannerException {
        if (m_interfaceTracker == null) {
            m_interfaceTracker = createTracker(InterfaceTracker.class, nodeIpAddress);
        }
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.ResourceScanner#scanResource(org.opennms.netmgt.model.OnmsResource, java.io.File, java.net.InetAddress)
     */
    public void scanResource(OnmsResource resource, File resourceDir, InetAddress nodeIpAddress) throws ResourceScannerException {
        initialize(nodeIpAddress);
        LOG.debug("scanResource: processing resource {}", resource.getId());
        try {
            StringTokenizer token = new StringTokenizer(resource.getName(), ".");
            String ifIndexStr = token.nextToken();
            String proIndexStr = token.nextToken();

            int ifIndex = Integer.parseInt(ifIndexStr);
            String ifName =  m_interfaceTracker.getIfName(ifIndex);
            String ifAlias  =  m_interfaceTracker.getIfAlias(ifIndex);
            LOG.debug("scanResource: interfaceIndex={}, protocolIndex={}", ifIndexStr, proIndexStr);

            ResourceTypeUtils.updateStringProperty(resourceDir, ifName, PARM_NBAR_PORT);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifAlias, PARM_NBAR_PORT_ALIAS);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifIndexStr, PARM_NBAR_PORT_INDEX);
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
    }
}
