package org.opennms.netmgt.provision.resourcescanners.trackers;

import org.opennms.netmgt.provision.service.snmp.SnmpTableEntry;
import org.opennms.netmgt.snmp.NamedSnmpVar;
import org.opennms.netmgt.snmp.AbstractSnmpStore;

/**
 * The Class Interface Tracker Entry.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public final class InterfaceTrackerEntry extends SnmpTableEntry {

    /** The Constant IF_INDEX. */
    public final static String IF_INDEX = AbstractSnmpStore.IFINDEX;

    /** The Constant IF_DESCR. */
    public final static String IF_DESCR = "ifDescr";

    /** The Constant IF_TYPE. */
    public final static String IF_TYPE = "ifType";

    /** The Constant IF_SPEED. */
    public final static String IF_SPEED = "ifSpeed";

    /** The Constant IF_PHYS_ADDR. */
    public final static String IF_PHYS_ADDR = "ifPhysAddr";

    /** The Constant IF_NAME. */
    public final static String IF_NAME = "ifName";

    /** The Constant IF_ALIAS. */
    public final static String IF_ALIAS = "ifAlias";

    /** The SNMP element list. */
    public static NamedSnmpVar[] ms_elemList = new NamedSnmpVar[] {
        new NamedSnmpVar(NamedSnmpVar.SNMPINT32, IF_INDEX, ".1.3.6.1.2.1.2.2.1.1", 1),
        new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING, IF_DESCR, ".1.3.6.1.2.1.2.2.1.2", 2),
        new NamedSnmpVar(NamedSnmpVar.SNMPINT32, IF_TYPE, ".1.3.6.1.2.1.2.2.1.3", 3),
        new NamedSnmpVar(NamedSnmpVar.SNMPGAUGE32, IF_SPEED, ".1.3.6.1.2.1.2.2.1.5", 4),
        new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING, IF_PHYS_ADDR, ".1.3.6.1.2.1.2.2.1.6", 5),
        new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING, IF_NAME, ".1.3.6.1.2.1.31.1.1.1.1", 6),
        new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING, IF_ALIAS, ".1.3.6.1.2.1.31.1.1.1.18", 7),      
    };

    /**
     * Instantiates a new interface tracker entry.
     */
    public InterfaceTrackerEntry() {
        super(ms_elemList);
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.snmp.AbstractSnmpStore#getIfIndex()
     */
    public Integer getIfIndex() {
        return getInt32(IF_INDEX);
    }

    /**
     * Gets the interface type.
     * 
     * @return the interface type
     */
    public Integer getIfType() {
        return getInt32(IF_TYPE);
    }

    /**
     * Gets the interface description.
     * 
     * @return the interface description
     */
    public String getIfDescr() {
        return getDisplayString(IF_DESCR);
    }

    /**
     * Gets the interface speed.
     * 
     * @return the interface speed
     */
    public Long getIfSpeed() {
        return getUInt32(IF_SPEED);
    }

    /**
     * Gets the interface name.
     * 
     * @return the interface name
     */
    public String getIfName() {
        return getDisplayString(IF_NAME);
    }

    /**
     * Gets the interface alias.
     * 
     * @return the interface alias
     */
    public String getIfAlias() {
        return getDisplayString(IF_ALIAS);
    }

    /**
     * Gets the physical address.
     * 
     * @return the physical address
     */
    public String getPhysAddr() {
        return getHexString(IF_PHYS_ADDR);
    }

}
