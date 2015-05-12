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
 * The Test Class for SnmpCiscoCpuScanner.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class SnmpCiscoCpuScannerTest extends AbstractSnmpScannerTest {

    /**
     * Test scanner.
     *
     * @throws Exception the exception
     */
    @Test
    @JUnitSnmpAgent(host="192.168.1.1", port=1161, resource="classpath:c7200.atm.properties")
    public void testScanner() throws Exception {
        OnmsResourceType type = new GenericIndexResourceType(null, "cpmCPUTotalIndex", "Cisco CPU", null, null);
        File resourceDir = createResourceDir(1, type, "1");
        OnmsResource resource = createOnmsResource(1, type, "1", new String[] {"cpmTotal"});

        ResourceScanner scanner = ResourceScannerFactory.getResourceScanner(type.getName());
        scanner.scanResource(resource, resourceDir, InetAddressUtils.addr("192.168.1.1"));
        Assert.assertEquals("7206VXR chassis, Hw Serial#: 4294967295, Hw Revision: A", ResourceTypeUtils.getStringProperty(resourceDir, SnmpCiscoCpuScanner.PARM_CPU_NAME));

        FileUtils.deleteDirectory(resourceDir);
    }

}
