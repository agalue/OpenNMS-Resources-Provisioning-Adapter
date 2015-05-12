package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.net.InetAddress;

import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.provision.ResourceScannerSource;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.opennms.netmgt.provision.resourcescanners.trackers.CiscoQoSTracker;
import org.opennms.netmgt.provision.resourcescanners.trackers.InterfaceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Resource Scanner for Cisco QoS.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@ResourceScannerSource("cbQosCMStatsIndex")
public class SnmpCiscoQoSScanner extends AbstractSnmpResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpCiscoQoSScanner.class);

    /** The Constant Cisco QoS Policy. */
    public static final String PARM_CCM_POLICY = "cbQosClassMapPolicy";

    /** The Constant Cisco QoS Class-Map name. */
    public static final String PARM_CCM_CLASSMAP = "cbQosClassMapName";

    /** The Constant Cisco QoS Interface Name. */
    public static final String PARM_CCM_INTERFACE = "cbQosClassMapInterface";

    /** The Constant Cisco QoS Interface Alias. */
    public static final String PARM_CCM_INTERFACE_ALIAS = "cbQosClassMapInterfaceAlias";

    /** The Constant Cisco QoS Interface ifIndex. */
    public static final String PARM_CCM_INTERFACE_INDEX = "cbQosClassMapInterfaceIfIndex";

    /** The Interface tracker. */
    protected InterfaceTracker m_interfaceTracker;

    /** The Cisco QoS tracker. */
    protected CiscoQoSTracker m_ciscoQosTracker;

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
        if (m_ciscoQosTracker == null) {
            m_ciscoQosTracker = createTracker(CiscoQoSTracker.class, nodeIpAddress);
        }
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.ResourceScanner#scanResource(org.opennms.netmgt.model.OnmsResource, java.io.File, java.net.InetAddress)
     */
    public void scanResource(OnmsResource resource, File resourceDir, InetAddress nodeIpAddress) throws ResourceScannerException {
        initialize(nodeIpAddress);
        LOG.debug("scanResource: processing resource {}", resource.getId());
        try {
            String oidIndex = resource.getName();
            // Find Policy Information
            String[] indexes = oidIndex.split("\\.");
            if (indexes.length < 2) {
                throw new ResourceScannerException("Malformed Cisco QoS Object Index %s for %s ", oidIndex, resource.getId());
            }
            String policyIndex = indexes[0];
            String objectsIndex = indexes[1];
            String parentIndex = objectsIndex;
            String searchIndex = null;
            boolean found = false;
            do {
                searchIndex = policyIndex + "." + parentIndex;
                parentIndex = m_ciscoQosTracker.getParentIndex(searchIndex);
                String type = m_ciscoQosTracker.getConfigType(searchIndex);
                found = type.equals("1"); // Is a policy
                LOG.debug("scanResource: parent index for {} is {}, is a policy? {}", searchIndex, parentIndex, found);
            } while (!found);
            LOG.debug("scanResource: retrieving policy config index for {}", searchIndex);
            String policyConfigIndex = m_ciscoQosTracker.getConfigIndex(searchIndex);
            LOG.debug("scanResource: found policy config index {}", policyConfigIndex);
            String policyName = m_ciscoQosTracker.getPolicyName(policyConfigIndex);
            if (policyName == null) {
                throw new ResourceScannerException("Can't find policy name using index %s for resource %s", policyConfigIndex, resource);
            }
            LOG.debug("scanResource: policyIndex={}, policyConfigIndex={}, policyName={}", policyIndex, policyConfigIndex, policyName);

            // Find ClassMap Information
            String classMapCfgIndex = m_ciscoQosTracker.getConfigIndex(oidIndex);
            if (classMapCfgIndex == null) {
                throw new ResourceScannerException("Can't find class-map config index for resource %s", resource);
            }
            String classMapName = m_ciscoQosTracker.getClassMapName(classMapCfgIndex);
            if (classMapName == null) {
                throw new ResourceScannerException("Can't find class-map name using index %s for resource %s", classMapCfgIndex, resource);
            }
            LOG.debug("scanResource: classMapCfgIndex={}, classMapName={}", classMapCfgIndex, classMapName);

            // Find Interface Information
            String interfaceIndex = m_ciscoQosTracker.getInterfaceIndex(policyIndex);
            if (interfaceIndex == null) {
                throw new ResourceScannerException("Can't find interface index for resource %s", resource);
            }
            int ifIndex = Integer.parseInt(interfaceIndex);
            String ifName =  m_interfaceTracker.getIfName(ifIndex);
            String ifAlias  =  m_interfaceTracker.getIfAlias(ifIndex);
            LOG.debug("scanResource: interfaceIndex={}, ifName={}, ifAlias={}", interfaceIndex, ifName, ifAlias);

            ResourceTypeUtils.updateStringProperty(resourceDir, policyName, PARM_CCM_POLICY);
            ResourceTypeUtils.updateStringProperty(resourceDir, classMapName, PARM_CCM_CLASSMAP);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifName, PARM_CCM_INTERFACE);
            ResourceTypeUtils.updateStringProperty(resourceDir, ifAlias, PARM_CCM_INTERFACE_ALIAS);
            ResourceTypeUtils.updateStringProperty(resourceDir, interfaceIndex, PARM_CCM_INTERFACE_INDEX);
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
    }
}
