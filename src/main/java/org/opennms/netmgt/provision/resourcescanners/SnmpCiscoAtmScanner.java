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
 * The Resource Scanner for Cisco ATM Interfaces.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@ResourceScannerSource("cAal5VccIndex")
public class SnmpCiscoAtmScanner extends AbstractSnmpResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpCiscoAtmScanner.class);

    /** The Constant Cisco AAL5 VPI. */
    public static final String PARM_ATM_VPI = "cAal5VccVpi";

    /** The Constant Cisco AAL5 VCI. */
    public static final String PARM_ATM_VCI = "cAal5VccVci";

    /** The Constant Cisco AAL5 Sub-Interface. */
    public static final String PARM_ATM_SUBIF = "cAal5VccSubif";

    /** The Constant Cisco AAL5 Interface. */
    public static final String PARM_ATM_INTF = "cAal5VccIntf";

    /** The Constant Cisco AAL5 Alias. */
    public static final String PARM_ATM_ALIAS = "cAal5VccAlias";

    /** The interface tracker. */
    private InterfaceTracker m_interfaceTracker;

    /**
     * Initialize.
     *
     * @param nodeIpAddress the node IP address
     * @throws ResourceScannerException the resource scanner exception
     */
    private void initialize(InetAddress nodeIpAddress) throws ResourceScannerException {
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
            StringTokenizer st = new StringTokenizer(resource.getName(), ".");
            String ifIndexStr = st.nextToken();
            String vpi = st.nextToken();
            String vci = st.nextToken();

            int ifIndex = Integer.parseInt(ifIndexStr);
            String ifName = m_interfaceTracker.getIfName(ifIndex);
            String ifAlias = m_interfaceTracker.getIfAlias(ifIndex);

            ResourceTypeUtils.updateStringProperty(resourceDir, ifIndexStr, PARM_ATM_SUBIF);
            ResourceTypeUtils.updateStringProperty(resourceDir, vpi, PARM_ATM_VPI);
            ResourceTypeUtils.updateStringProperty(resourceDir, vci, PARM_ATM_VCI);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifName, PARM_ATM_INTF);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifAlias, PARM_ATM_ALIAS);
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
    }

}
