package org.opennms.netmgt.provision.resourcescanners.trackers;

import java.net.InetAddress;

import org.opennms.netmgt.provision.service.snmp.SnmpTable;
import org.opennms.netmgt.snmp.SnmpInstId;
import org.opennms.netmgt.snmp.SnmpObjId;

/**
 * The Class Entity Table.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public final class EntityTracker extends SnmpTable<EntityTrackerEntry> {

    /**
     * Instantiates a new entity table.
     * 
     * @param address the address
     */
    public EntityTracker(InetAddress address) {
        super(address, "entityTable", EntityTrackerEntry.ms_elemList);
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.capsd.snmp.SnmpTable#createTableEntry(org.opennms.netmgt.snmp.SnmpObjId, org.opennms.netmgt.snmp.SnmpInstId, java.lang.Object)
     */
    protected EntityTrackerEntry createTableEntry(SnmpObjId base, SnmpInstId inst, Object val) {
        return new EntityTrackerEntry(inst.toString());
    }

    /**
     * Gets the label.
     * 
     * @param oidIndex the OID index
     * 
     * @return the label
     */
    public String getLabel(String oidIndex) {
        if (getEntries() != null) {
            for (EntityTrackerEntry entry : getEntries()) {
                if (entry.getOidIndex().equals(oidIndex)) {
                    return entry.getLabel();
                }
            }
        }
        return null;
    }
}
