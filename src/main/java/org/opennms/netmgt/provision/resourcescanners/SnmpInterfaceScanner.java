package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.net.InetAddress;
import java.util.Properties;

import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.provision.ResourceScannerSource;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.opennms.netmgt.provision.resourcescanners.trackers.InterfaceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Resource Scanner for SNMP Interfaces.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@ResourceScannerSource("interfaceSnmp")
public class SnmpInterfaceScanner extends AbstractSnmpResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpInterfaceScanner.class);

    /** The Constant PARM_INTF_TYPE_NAME. */
    public static final String PARM_INTF_TYPE_NAME = "ifTypeName";

    /** The Constant PARM_INTF_ALIAS_RAW. */
    public static final String PARM_INTF_ALIAS_RAW = "ifAliasRaw";

    /** The Interface Tracker. */
    private InterfaceTracker m_interfaceTracker;

    /** The ifType property mapping. */
    private Properties m_ifTypes;

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
        if (m_ifTypes == null) {
            m_ifTypes = ScannerUtils.loadProperties("/iftypes.properties");
        }
    }

    /**
     * Gets the ifIndex for an InterfaceSnmp Resource.
     *
     * @param resource the OpenNMS SNMP Interface Resource
     * @return the ifIndex
     * @throws ResourceScannerException the resource scanner exception
     */
    private int getIfIndex(OnmsResource resource) throws ResourceScannerException {
        if (!resource.getResourceType().getName().equals("interfaceSnmp")) {
            throw new ResourceScannerException("Resource %s is not a valid SNMP Interface", resource.getId());
        }
        String ifName = resource.getName().replaceFirst("-.*", "");
        return m_interfaceTracker.getIndexFromIfName(ifName);
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.ResourceScanner#scanResource(org.opennms.netmgt.model.OnmsResource, java.io.File, java.net.InetAddress)
     */
    public void scanResource(OnmsResource resource, File resourceDir, InetAddress nodeIpAddress) throws ResourceScannerException {
        initialize(nodeIpAddress);
        LOG.debug("scanResource: processing resource {}", resource.getId());
        try {
            int ifIndex = getIfIndex(resource);
            if (ifIndex < 0) {
                throw new ResourceScannerException("Can't find ifIndex for resource " + resource.getId());
            }
            String ifAlias = m_interfaceTracker.getIfAlias(ifIndex);
            String ifType = m_interfaceTracker.getIfType(ifIndex).toString();
            String ifTypeName = m_ifTypes.getProperty(ifType, "Unknown("+ifType+")");
            LOG.debug("scanResource: ifType={}, ifAlias={}", ifTypeName, ifAlias);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifTypeName, PARM_INTF_TYPE_NAME);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifAlias, PARM_INTF_ALIAS_RAW);
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
    }

}
