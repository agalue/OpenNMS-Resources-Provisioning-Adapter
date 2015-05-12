package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.net.InetAddress;
import java.util.StringTokenizer;

import org.opennms.core.utils.AlphaNumeric;
import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.provision.ResourceScannerSource;
import org.opennms.netmgt.provision.ResourceScannerException;
import org.opennms.netmgt.provision.resourcescanners.trackers.InterfaceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Resource Scanner for RFC1315 Frame Relay.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@ResourceScannerSource("frCircuitIfIndex")
public class SnmpFrameRelayScanner extends AbstractSnmpResourceScanner {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(SnmpFrameRelayScanner.class);

    /** The Constant Frame Relay Source Interface. */
    public static final String PARM_FR_SOURCE_CINTF = "frSrcIntf";

    /** The Constant Frame Relay Description. */
    public static final String PARM_FR_DESC = "frDescription";

    /** The Constant Frame Relay PVC. */
    public static final String PARM_FR_PVC = "frPvc";

    /** The Constant Frame Relay CIR. */
    public static final String PARM_FR_CIR = "frCir";

    /** The Constant Frame Relay EIR. */
    public static final String PARM_FR_EIR = "frEir";

    /** The Constant Frame Relay DLCI. */
    public static final String PARM_FR_DLCI = "frDlci";

    /** The Constant Frame Relay Sub-Interface ifIndex. Must exist on datacollection-config.xml. */
    public static final String PARM_FR_SUBIFINDEX = "frSubifIndex";

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
            String srcIfIndex = token.nextToken();
            String dlci = token.nextToken();

            String ifName = null;
            if (isNumeric(srcIfIndex)) {
                int ifIndex = Integer.parseInt(srcIfIndex);
                ifName =  m_interfaceTracker.getIfName(ifIndex);
            } else {
                ifName = srcIfIndex;
            }
            LOG.debug("scanResource: ifName={}, DLCI={}", ifName, dlci);

            ResourceTypeUtils.updateStringProperty(resourceDir, ifName, PARM_FR_SOURCE_CINTF);
            ResourceTypeUtils.updateStringProperty(resourceDir, dlci, PARM_FR_DLCI);

            // Getting DLCI Description and Speed
            int index = 0;
            String subIfIndex = ResourceTypeUtils.getStringProperty(resourceDir, PARM_FR_SUBIFINDEX);
            if (subIfIndex != null) {
                index = Integer.parseInt(subIfIndex);
            }
            if (index == 0) {
                // Cisco Devices Only. RULE: SubInterfaceNumber == DlciNumber
                String name = AlphaNumeric.parseAndReplace(ifName + '.' + dlci, '_');
                LOG.debug("scanResource: generating Description for Cisco Devices based on the rule 'sub-interface-id=dlci' using {}", name);
                index = m_interfaceTracker.getIndexFromIfName(name);
            }
            long pvc = 0, cir = 0, eir = 0;
            String description = "Unknown";
            if (index > 0) {
                String alias = m_interfaceTracker.getIfAlias(index);
                if (alias == null) {
                    LOG.warn("scanResource: no alias found for ifIndex {} for resource {}", index, resource.getId());
                } else {
                    description = alias;
                }
                // By default we use this rule: PVC = CIR expressed in bits per second
                pvc = m_interfaceTracker.getIfSpeed(index);
                cir = pvc;
                LOG.debug("scanResource: setting desc={} and speed={} for resource {}", alias, cir, resource.getId());
            } else {
                LOG.warn("scanResource: can't find the ifIndex of the sub-interface for resource {}", resource.getId());
            }
            ResourceTypeUtils.updateStringProperty(resourceDir, description, PARM_FR_DESC);
            ResourceTypeUtils.updateStringProperty(resourceDir, Long.toString(pvc), PARM_FR_PVC);
            ResourceTypeUtils.updateStringProperty(resourceDir, Long.toString(cir), PARM_FR_CIR);
            ResourceTypeUtils.updateStringProperty(resourceDir, Long.toString(eir), PARM_FR_EIR);
        } catch (Exception e) {
            throw new ResourceScannerException(e.getMessage(), e);
        }
    }

    /**
     * Checks if is numeric.
     *
     * @param text the text
     * @return true, if is numeric
     */
    private static boolean isNumeric(String text){
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }
}
