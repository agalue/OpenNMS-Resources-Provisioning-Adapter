package org.opennms.netmgt.provision.resourcescanners.trackers;

import java.net.InetAddress;
import org.opennms.core.utils.AlphaNumeric;
import org.opennms.netmgt.provision.service.snmp.SnmpTable;
import org.opennms.netmgt.snmp.SnmpInstId;
import org.opennms.netmgt.snmp.SnmpObjId;

/**
 * The Class InterfaceTracker.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public final class InterfaceTracker extends SnmpTable<InterfaceTrackerEntry> {

    /**
     * Instantiates a new interface tracker.
     * 
     * @param address the address
     */
    public InterfaceTracker(InetAddress address) {
        super(address, "interfaceTracker", InterfaceTrackerEntry.ms_elemList);
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.capsd.snmp.SnmpTable#createTableEntry(org.opennms.netmgt.snmp.SnmpObjId, org.opennms.netmgt.snmp.SnmpInstId, java.lang.Object)
     */
    protected InterfaceTrackerEntry createTableEntry(SnmpObjId base, SnmpInstId inst, Object val) {
        return new InterfaceTrackerEntry();
    }

    /**
     * Gets the ifIndex from ifName and/or ifDescr.
     * 
     * @param name the interface name
     * 
     * @return the SNMP interface index (ifIndex)
     */
    public int getIndexFromIfName(String name) {
        if (getEntries() != null) {
            for (InterfaceTrackerEntry entry : getEntries()) {
                String ifName = entry.getIfName();
                if (ifName == null)
                    ifName = entry.getIfDescr();
                if (ifName != null) {
                    String normalized = AlphaNumeric.parseAndReplace(ifName, '_');
                    if (normalized.equals(name))
                        return entry.getIfIndex();
                }
            }
        }
        return -1;
    }

    /**
     * Gets the SNMP interface alias (ifAlias).
     * 
     * @param ifIndex the SNMP interface index (ifIndex)
     * 
     * @return the interface alias
     */
    public String getIfAlias(final int ifIndex) {
        String defaultAlias = "Unknown(" + ifIndex + ")";
        if (getEntries() != null) {
            for (InterfaceTrackerEntry entry : getEntries()) {
                Integer ndx = entry.getIfIndex();
                if (ndx != null && ndx.intValue() == ifIndex) {
                    return entry.getIfAlias() == null ? defaultAlias : entry.getIfAlias();
                }
            }
        }
        return defaultAlias;
    }

    /**
     * Gets the SNMP interface name (ifName).
     * 
     * @param ifIndex the SNMP interface index (ifIndex)
     * 
     * @return the interface name
     */
    public String getIfName(final int ifIndex) {
        String defaultName = "Unknown(" + ifIndex + ")";
        if (getEntries() != null) {
            for (InterfaceTrackerEntry entry : getEntries()) {
                Integer ndx = entry.getIfIndex();
                if (ndx != null && ndx.intValue() == ifIndex) {
                    return entry.getIfName() == null ? defaultName : entry.getIfName();
                }
            }
        }
        return defaultName;
    }

    /**
     * Gets the SNMP interface description (ifDescr).
     * 
     * @param ifIndex the SNMP interface index (ifIndex)
     * 
     * @return the interface description
     */
    public String getIfDescr(final int ifIndex) {
        String defaultDescr = "Unknown(" + ifIndex + ")";
        if (getEntries() != null) {
            for (InterfaceTrackerEntry entry : getEntries()) {
                Integer ndx = entry.getIfIndex();
                if (ndx != null && ndx.intValue() == ifIndex) {
                    return entry.getIfDescr() == null ? defaultDescr : entry.getIfDescr();
                }
            }
        }
        return defaultDescr;
    }

    /**
     * Gets the SNMP interface speed (ifSpeed).
     * 
     * @param ifIndex the SNMP interface index (ifIndex)
     * 
     * @return the interface speed
     */
    public Long getIfSpeed(final int ifIndex) {
        if (getEntries() != null) {
            for (InterfaceTrackerEntry entry : getEntries()) {
                Integer ndx = entry.getIfIndex();
                if (ndx != null && ndx.intValue() == ifIndex) {
                    return entry.getIfSpeed();
                }
            }
        }
        return 0l;
    }

    /**
     * Gets the SNMP interface type (ifType).
     * 
     * @param ifIndex the SNMP interface index (ifIndex)
     * 
     * @return the interface type
     */
    public Integer getIfType(final int ifIndex) {
        if (getEntries() != null) {
            for (InterfaceTrackerEntry entry : getEntries()) {
                Integer ndx = entry.getIfIndex();
                if (ndx != null && ndx.intValue() == ifIndex) {
                    return entry.getIfType();
                }
            }
        }
        return 0;
    }

    /**
     * Gets the interface label (based on ifName and physical address).
     *
     * @param ifIndex the SNMP interface index (ifIndex)
     * @return the interface label (or noLabel_{ifIndex} if does not exist)
     */
    public String getLabel(final String ifIndex) {
        int index = Integer.parseInt(ifIndex);
        if (getEntries() != null) {
            for (InterfaceTrackerEntry entry : getEntries()) {
                Integer ndx = entry.getIfIndex();
                if (ndx != null && ndx.intValue() == index) {
                    String name = "noLabel_" + ifIndex;
                    if (entry.getIfName() != null && !entry.getIfName().equals("")) {
                        name = AlphaNumeric.parseAndReplace(entry.getIfName(), '_');
                    } else {
                        name = AlphaNumeric.parseAndReplace(entry.getIfDescr(), '_');
                    }
                    if (entry.getPhysAddr() != null && !entry.getPhysAddr().equals("")) {
                        return name + '-' + AlphaNumeric.parseAndTrim(entry.getPhysAddr());
                    }
                    return name;
                }
            }
        }
        return "noLabel_" + ifIndex;
    }
}
