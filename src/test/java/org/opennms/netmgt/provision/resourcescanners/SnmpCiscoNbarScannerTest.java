package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennms.core.test.snmp.annotations.JUnitSnmpAgent;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.dao.support.GenericIndexResourceType;
import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.OnmsResourceType;
import org.opennms.netmgt.provision.ResourceScanner;
import org.opennms.netmgt.provision.ResourceScannerFactory;

/**
 * The Test Class for SnmpCiscoNbarScanner.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SnmpCiscoNbarScannerTest extends AbstractSnmpScannerTest {

    /**
     * Test scanner.
     *
     * @throws Exception the exception
     */
    @Test
    @JUnitSnmpAgent(host="192.168.1.1", port=1161, resource="classpath:c7200.nbar.properties")
    public void testScanner() throws Exception {
        OnmsResourceType type = new GenericIndexResourceType(null, "ciscoNbarProtocolIndex", "Cisco NBAR", null, null);
        File resourceDir = createResourceDir(1, type, "2.60");
        OnmsResource resource = createOnmsResource(1, type, "2.603", new String[] {"ifInOctets","ifOutOctets"});

        ResourceScanner scanner = ResourceScannerFactory.getResourceScanner(type.getName());
        scanner.scanResource(resource, resourceDir, InetAddressUtils.addr("192.168.1.1"));
        Assert.assertEquals("Fa0/0", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoNbarScanner.PARM_NBAR_PORT));
        Assert.assertEquals("Main LAN", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoNbarScanner.PARM_NBAR_PORT_ALIAS));
        Assert.assertEquals("2", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoNbarScanner.PARM_NBAR_PORT_INDEX));

        FileUtils.deleteDirectory(resourceDir);
    }

}
