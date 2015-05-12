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
 * The Test Class for SnmpCiscoQosScanner.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SnmpCiscoQosScannerTest extends AbstractSnmpScannerTest {

    /**
     * Test scanner.
     *
     * @throws Exception the exception
     */
    @Test
    @JUnitSnmpAgent(host="192.168.1.1", port=1161, resource="classpath:c7200.qos.properties")
    public void testScanner() throws Exception {
        OnmsResourceType type = new GenericIndexResourceType(null, "cbQosCMStatsIndex", "Cisco QoS", null, null);
        File resourceDir = createResourceDir(1, type, "4439.6167");
        OnmsResource resource = createOnmsResource(1, type, "4439.6167", new String[] {"ccmDropByte64"});

        ResourceScanner scanner = ResourceScannerFactory.getResourceScanner(type.getName());
        scanner.scanResource(resource, resourceDir, InetAddressUtils.addr("192.168.1.1"));
        Assert.assertEquals("WAN-QOS-PEPSI", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoQoSScanner.PARM_CCM_POLICY));
        Assert.assertEquals("Volumen-LotusNotes", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoQoSScanner.PARM_CCM_CLASSMAP));
        Assert.assertEquals("Se6/0.994", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoQoSScanner.PARM_CCM_INTERFACE));
        Assert.assertEquals("234", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoQoSScanner.PARM_CCM_INTERFACE_INDEX));
        Assert.assertEquals("PEPSI (LOS CORTIJOS) (0212) 2324443/2324601 CKTO:2213/014197 DLC", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoQoSScanner.PARM_CCM_INTERFACE_ALIAS));

        FileUtils.deleteDirectory(resourceDir);
    }

}
