package org.opennms.netmgt.provision.resourcescanners.trackers;

import org.opennms.netmgt.provision.service.snmp.SnmpTableEntry;
import org.opennms.netmgt.snmp.NamedSnmpVar;

/**
 * The Class Cisco QoS Table Entry.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public final class CiscoQoSTrackerEntry extends SnmpTableEntry {

    /** The Constant CONF_INDEX. */
    private final static String CONF_INDEX = "cbQosConfigIndex";

    /** The Constant CONF_TYPE. */
    private final static String CONF_TYPE = "cbQosConfigType";

    /** The Constant PARENT_INDEX. */
    private final static String PARENT_INDEX = "cbQosParentIndex";

    /** The Constant POLICY_NAME. */
    private final static String POLICY_NAME = "cbQosPolicyMapDesc";

    /** The Constant CLASS_NAME. */
    private final static String CLASS_NAME = "cbQosClassMapName";

    /** The Constant INTF_INDEX. */
    private final static String INTF_INDEX = "cbQosIfIndex";

    /** The OID index. */
    private String oidIndex = null;

    /** The SNMP element list. */
    public static NamedSnmpVar[] ms_elemList = new NamedSnmpVar[] {
        // From cbQosObjectsTable - To obtain the configIndex (should be recursive using parentIndex)
        new NamedSnmpVar(NamedSnmpVar.SNMPUINT32, CONF_INDEX, ".1.3.6.1.4.1.9.9.166.1.5.1.1.2", 1),
        new NamedSnmpVar(NamedSnmpVar.SNMPUINT32, CONF_TYPE, ".1.3.6.1.4.1.9.9.166.1.5.1.1.3", 2),
        new NamedSnmpVar(NamedSnmpVar.SNMPUINT32, PARENT_INDEX, ".1.3.6.1.4.1.9.9.166.1.5.1.1.4", 4),
        // From cbQosServicePolicyTable (policy configuration: ifIndex)
        new NamedSnmpVar(NamedSnmpVar.SNMPINT32, INTF_INDEX, ".1.3.6.1.4.1.9.9.166.1.1.1.1.4", 5),
        // From cbQosPolicyMapCfgTable - To retrieve the policy name
        new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING, POLICY_NAME, ".1.3.6.1.4.1.9.9.166.1.6.1.1.1", 5),
        // From cbQosCMCfgTable - To retrieve the class-map name
        new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING, CLASS_NAME, ".1.3.6.1.4.1.9.9.166.1.7.1.1.1", 6)
    };

    /**
     * Instantiates a new Cisco QoS Table entry.
     * 
     * @param oidIndex the OID index
     */
    public CiscoQoSTrackerEntry(final String oidIndex) {
        super(ms_elemList);
        this.oidIndex = oidIndex;
    }

    /**
     * Gets the OID index.
     * 
     * @return the OID index
     */
    public String getOidIndex() {
        return this.oidIndex;
    }

    /**
     * Gets the configuration index.
     * 
     * @return the configuration index
     */
    public String getConfigIndex() {
        return getDisplayString(CONF_INDEX);
    }

    /**
     * Gets the configuration type.
     * 
     * @return the configuration type
     */
    public String getConfigType() {
        return getDisplayString(CONF_TYPE);
    }

    /**
     * Gets the parent index.
     * 
     * @return the parent index
     */
    public String getParentIndex() {
        return getDisplayString(PARENT_INDEX);
    }

    /**
     * Gets the policy name.
     * 
     * @return the policy name
     */
    public String getPolicyName() {
        return getDisplayString(POLICY_NAME);
    }

    /**
     * Gets the class map name.
     * 
     * @return the class map name
     */
    public String getClassMapName() {
        return getDisplayString(CLASS_NAME);
    }

    /**
     * Gets the interface index.
     * 
     * @return the interface index
     */
    public String getInterfaceIndex() {
        return getDisplayString(INTF_INDEX);
    }

}
