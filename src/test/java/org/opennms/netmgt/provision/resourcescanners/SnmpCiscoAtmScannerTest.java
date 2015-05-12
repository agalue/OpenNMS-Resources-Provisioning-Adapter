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
 * The Test Class for SnmpCiscoAtmScanner.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SnmpCiscoAtmScannerTest extends AbstractSnmpScannerTest {

    /**
     * Test scanner.
     *
     * @throws Exception the exception
     */
    @Test
    @JUnitSnmpAgent(host="192.168.1.1", port=1161, resource="classpath:c7200.atm.properties")
    public void testScanner() throws Exception {
        OnmsResourceType type = new GenericIndexResourceType(null, "cAal5VccIndex", "Cisco ATM", null, null);
        File resourceDir = createResourceDir(1, type, "11.1.103");
        OnmsResource resource = createOnmsResource(1, type, "11.1.103", new String[] {"ifInOctets","ifOutOctets"});

        ResourceScanner scanner = ResourceScannerFactory.getResourceScanner(type.getName());
        scanner.scanResource(resource, resourceDir, InetAddressUtils.addr("192.168.1.1"));
        Assert.assertEquals("11", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoAtmScanner.PARM_ATM_SUBIF));
        Assert.assertEquals("1", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoAtmScanner.PARM_ATM_VPI));
        Assert.assertEquals("103", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoAtmScanner.PARM_ATM_VCI));
        Assert.assertEquals("AT1/0.103", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoAtmScanner.PARM_ATM_INTF));
        Assert.assertEquals("Maracaibo", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoAtmScanner.PARM_ATM_ALIAS));

        FileUtils.deleteDirectory(resourceDir);
    }

}
