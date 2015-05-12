package org.opennms.netmgt.provision.resourcescanners.trackers;

import org.opennms.netmgt.provision.service.snmp.SnmpTableEntry;
import org.opennms.netmgt.snmp.NamedSnmpVar;

/**
 * The Class Entity Table Entry.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public final class EntityTrackerEntry extends SnmpTableEntry {

    /** The Constant LABEL. */
    private final static String LABEL = "entityName";

    /** The OID index. */
    private String oidIndex = null;

    /** The SNMP element list. */
    public static NamedSnmpVar[] ms_elemList = new NamedSnmpVar[] {
        new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING, LABEL, ".1.3.6.1.2.1.47.1.1.1.1.2", 1)
    };

    /**
     * Instantiates a new entity table entry.
     * 
     * @param oidIndex the OID index
     */
    public EntityTrackerEntry(final String oidIndex) {
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
     * Gets the label.
     * 
     * @return the label
     */
    public String getLabel() {
        return getDisplayString(LABEL);
    }

}
