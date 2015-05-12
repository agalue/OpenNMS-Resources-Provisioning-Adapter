package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.net.InetAddress;
import java.util.StringTokenizer;

import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.provision.ResourceScannerSource;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.opennms.netmgt.provision.resourcescanners.trackers.EntityTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Resource Scanner for SNMP Interfaces.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@ResourceScannerSource("cseL3VlanIndex")
public class SnmpCiscoVlanScanner extends AbstractSnmpResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpCiscoVlanScanner.class);

    /** The Constant Cisco Switch Vlan Entity. */
    public static final String PARM_CSW_ENTITY = "cseL3VlanEntity";

    /** The Constant Cisco Switch Vlan Name. */
    public static final String PARM_CSW_VLAN = "cseL3VlanName";

    /** The Entity Tracker. */
    private EntityTracker m_entityTracker;

    /**
     * Initialize.
     *
     * @param nodeIpAddress the node IP address
     * @throws ResourceScannerException the resource scanner exception
     */
    private void initialize(InetAddress nodeIpAddress) throws ResourceScannerException {
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
            StringTokenizer st = new StringTokenizer(resource.getName(), ".");
            String entityIndex = st.nextToken();
            String vlanIndex = st.nextToken();

            String entityName = m_entityTracker.getLabel(entityIndex);
            String entity = (entityName != null ? entityName : entityIndex);

            ResourceTypeUtils.updateStringProperty(resourceDir, vlanIndex, PARM_CSW_VLAN);
            ResourceTypeUtils.updateStringProperty(resourceDir, entity, PARM_CSW_ENTITY);
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
    }

}
