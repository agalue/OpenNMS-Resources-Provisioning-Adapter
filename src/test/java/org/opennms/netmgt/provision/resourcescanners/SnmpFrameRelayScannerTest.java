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
 * The Test Class for SnmpFrameRelayScanner.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SnmpFrameRelayScannerTest extends AbstractSnmpScannerTest {

    /**
     * Test scanner.
     *
     * @throws Exception the exception
     */
    @Test
    @JUnitSnmpAgent(host="192.168.1.1", port=1161, resource="classpath:c7200.nbar.properties")
    public void testScanner() throws Exception {
        OnmsResourceType type = new GenericIndexResourceType(null, "frCircuitIfIndex", "RFC1315 Frame-Relay", null, null);
        File resourceDir = createResourceDir(1, type, "4.102");
        OnmsResource resource = createOnmsResource(1, type, "4.102", new String[] {"frSentOctets","frReceivedOctets"});

        ResourceScanner scanner = ResourceScannerFactory.getResourceScanner(type.getName());
        scanner.scanResource(resource, resourceDir, InetAddressUtils.addr("192.168.1.1"));
        validate(resourceDir);
        Assert.assertEquals("Se1/0", ResourceTypeUtils.getStringProperty(resourceDir, SnmpFrameRelayScanner.PARM_FR_SOURCE_CINTF));

        resourceDir = createResourceDir(1, type, "Se1_0.102");
        resource = createOnmsResource(1, type, "Se1_0.102", new String[] {"frSentOctets","frReceivedOctets"});
        scanner.scanResource(resource, resourceDir, InetAddressUtils.addr("192.168.1.1"));
        validate(resourceDir);
        Assert.assertEquals("Se1_0", ResourceTypeUtils.getStringProperty(resourceDir, SnmpFrameRelayScanner.PARM_FR_SOURCE_CINTF));

        FileUtils.deleteDirectory(resourceDir);
    }

    private void validate(File resourceDir) {
        Assert.assertEquals("102", ResourceTypeUtils.getStringProperty(resourceDir, SnmpFrameRelayScanner.PARM_FR_DLCI));
        Assert.assertEquals("256000", ResourceTypeUtils.getStringProperty(resourceDir, SnmpFrameRelayScanner.PARM_FR_CIR));
        Assert.assertEquals("256000", ResourceTypeUtils.getStringProperty(resourceDir, SnmpFrameRelayScanner.PARM_FR_PVC));
        Assert.assertEquals("Conexion Valencia", ResourceTypeUtils.getStringProperty(resourceDir, SnmpFrameRelayScanner.PARM_FR_DESC));
    }

}
