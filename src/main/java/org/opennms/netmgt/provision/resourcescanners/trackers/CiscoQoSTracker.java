package org.opennms.netmgt.provision.resourcescanners.trackers;

import java.net.InetAddress;

import org.opennms.netmgt.provision.service.snmp.SnmpTable;
import org.opennms.netmgt.snmp.SnmpInstId;
import org.opennms.netmgt.snmp.SnmpObjId;

/**
 * The Class Cisco QoS Table.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public final class CiscoQoSTracker extends SnmpTable<CiscoQoSTrackerEntry> {

    /**
     * Instantiates a new Cisco QoS Table.
     * 
     * @param address the address
     */
    public CiscoQoSTracker(InetAddress address) {
        super(address, "ciscoQoSTable", CiscoQoSTrackerEntry.ms_elemList);
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.capsd.snmp.SnmpTable#createTableEntry(org.opennms.netmgt.snmp.SnmpObjId, org.opennms.netmgt.snmp.SnmpInstId, java.lang.Object)
     */
    protected CiscoQoSTrackerEntry createTableEntry(SnmpObjId base, SnmpInstId inst, Object val) {
        return new CiscoQoSTrackerEntry(inst.toString());
    }

    /**
     * Gets the configuration index.
     * 
     * @param oidIndex the OID index
     * 
     * @return the configuration index
     */
    public String getConfigIndex(String oidIndex) {
        if (getEntries() != null) {
            for (CiscoQoSTrackerEntry entry : getEntries()) {
                if (entry.getOidIndex().equals(oidIndex)) {
                    return entry.getConfigIndex();
                }
            }
        }
        return null;
    }

    /**
     * Gets the configuration type.
     * 
     * @param oidIndex the OID index
     * 
     * @return the configuration type
     */
    public String getConfigType(String oidIndex) {
        if (getEntries() != null) {
            for (CiscoQoSTrackerEntry entry : getEntries()) {
                if (entry.getOidIndex().equals(oidIndex)) {
                    return entry.getConfigType();
                }
            }
        }
        return null;
    }

    /**
     * Gets the parent index.
     * 
     * @param oidIndex the OID index
     * 
     * @return the parent index
     */
    public String getParentIndex(String oidIndex) {
        if (getEntries() != null) {
            for (CiscoQoSTrackerEntry entry : getEntries()) {
                if (entry.getOidIndex().equals(oidIndex)) {
                    return entry.getParentIndex();
                }
            }
        }
        return null;
    }

    /**
     * Gets the policy name.
     * 
     * @param oidIndex the OID index
     * 
     * @return the policy name
     */
    public String getPolicyName(String oidIndex) {
        if (getEntries() != null) {
            for (CiscoQoSTrackerEntry entry : getEntries()) {
                if (entry.getOidIndex().equals(oidIndex)) {
                    return formatString(entry.getPolicyName());
                }
            }
        }
        return null;
    }

    /**
     * Gets the class map name.
     * 
     * @param oidIndex the OID index
     * 
     * @return the class map name
     */
    public String getClassMapName(String oidIndex) {
        if (getEntries() != null) {
            for (CiscoQoSTrackerEntry entry : getEntries()) {
                if (entry.getOidIndex().equals(oidIndex)) {
                    return formatString(entry.getClassMapName());
                }
            }
        }
        return null;
    }

    /**
     * Gets the interface index.
     * 
     * @param oidIndex the OID index
     * 
     * @return the interface index
     */
    public String getInterfaceIndex(String oidIndex) {
        if (getEntries() != null) {
            for (CiscoQoSTrackerEntry entry : getEntries()) {
                if (entry.getOidIndex().equals(oidIndex)) {
                    return entry.getInterfaceIndex();
                }
            }
        }
        return null;
    }

    /**
     * Format a string, removing invalid characters.
     *
     * @param sourceString the source string
     * @return the formatted string
     */
    private String formatString(String sourceString) {
        return sourceString != null ? sourceString.replaceAll("\"", "") : null;
    }
}
