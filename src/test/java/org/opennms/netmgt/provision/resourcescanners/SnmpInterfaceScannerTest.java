package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennms.core.test.snmp.annotations.JUnitSnmpAgent;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.netmgt.dao.support.InterfaceSnmpResourceType;
import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.OnmsResourceType;
import org.opennms.netmgt.provision.ResourceScanner;
import org.opennms.netmgt.provision.ResourceScannerFactory;

/**
 * The Test Class for SnmpInterfaceScanner.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SnmpInterfaceScannerTest extends AbstractSnmpScannerTest {

    /**
     * Test if types.
     *
     * @throws Exception the exception
     */
    @Test
    public void testIfTypes() throws Exception {
        Properties ifTypes = new Properties();
        ifTypes.load(getClass().getResourceAsStream("/iftypes.properties"));
        Assert.assertEquals("ethernetCsmacd", ifTypes.getProperty("6", "unknown"));
    }

    /**
     * Test scanner.
     *
     * @throws Exception the exception
     */
    @Test
    @JUnitSnmpAgent(host="192.168.1.1", port=1161, resource="classpath:c7200.atm.properties")
    public void testScanner() throws Exception {
        OnmsResourceType type =  new InterfaceSnmpResourceType(null, null);
        File resourceDir = createResourceDir(1, type, "AT1_0_103");
        OnmsResource resource = createOnmsResource(1, type, "AT1_0_103", new String[] {"ifInOctets","ifOutOctets"});

        ResourceScanner scanner = ResourceScannerFactory.getResourceScanner(type.getName());
        scanner.scanResource(resource, resourceDir, InetAddressUtils.addr("192.168.1.1"));
        Assert.assertEquals("Maracaibo", ResourceTypeUtils.getStringProperty(resourceDir, SnmpInterfaceScanner.PARM_INTF_ALIAS_RAW));
        Assert.assertEquals("atmSubInterface", ResourceTypeUtils.getStringProperty(resourceDir, SnmpInterfaceScanner.PARM_INTF_TYPE_NAME));

        FileUtils.deleteDirectory(resourceDir);
    }

}
