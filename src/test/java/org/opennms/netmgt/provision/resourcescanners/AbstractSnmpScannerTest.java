package org.opennms.netmgt.provision.resourcescanners;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.opennms.core.test.MockLogAppender;
import org.opennms.core.test.snmp.JUnitSnmpAgentExecutionListener;
import org.opennms.netmgt.dao.support.InterfaceSnmpResourceType;
import org.opennms.netmgt.dao.support.NodeResourceType;
import org.opennms.netmgt.dao.support.NodeSnmpResourceType;
import org.opennms.netmgt.model.OnmsAttribute;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.OnmsResourceType;
import org.opennms.netmgt.model.RrdGraphAttribute;
import org.opennms.netmgt.provision.ResourceScannerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * The abstract test class for SNMP Resource Scanners.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
    JUnitSnmpAgentExecutionListener.class,
    DirtiesContextTestExecutionListener.class
})
@ContextConfiguration(locations= {
        "classpath:/META-INF/opennms/applicationContext-proxy-snmp.xml",
})
public abstract class AbstractSnmpScannerTest {

    /**
     * Sets up the test
     * 
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        ResourceScannerFactory.init();
        MockLogAppender.setupLogging();
    }

    /**
     * Tears down the test
     * 
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
        MockLogAppender.assertNoWarningsOrGreater();
    }

    /**
     * Creates the OpenNMS resource.
     *
     * @param nodeId the node id
     * @param resourceType the resource type
     * @param resourceName the resource name
     * @param rrdAttributes the RRD attributes
     * @return the OpenNMS resource
     * @throws Exception the exception
     */
    protected OnmsResource createOnmsResource(int nodeId, OnmsResourceType resourceType, String resourceName, String[] rrdAttributes) throws Exception {
        File resourceDir = createResourceDir(nodeId, resourceType, resourceName);
        Set<OnmsAttribute> attribtues = new HashSet<OnmsAttribute>();
        for (String attribute : rrdAttributes) {
            attribtues.add(new RrdGraphAttribute(attribute, nodeId + "/" + resourceType + "/" + resourceName, attribute + ".jrb"));
        }
        OnmsResource resource = new OnmsResource(resourceName, resourceName, resourceType, attribtues);
        OnmsResource node = new OnmsResource(Integer.toString(nodeId), "node", new NodeResourceType(null), attribtues, Collections.singletonList(resource));
        resource.setParent(node);
        Properties properties = new Properties();
        properties.store(new FileOutputStream(new File(resourceDir, "strings.properties")), "JUnit test");
        return resource;
    }

    /**
     * Creates the resource directory.
     *
     * @param nodeId the node id
     * @param resourceType the resource type
     * @param resourceName the resource name
     * @return the file
     */
    protected File createResourceDir(Integer nodeId, OnmsResourceType resourceType, String resourceName) {
        File resourceDir = null;
        if (resourceType instanceof NodeSnmpResourceType)
            resourceDir = new File("target/snmp/" + nodeId);
        else if (resourceType instanceof InterfaceSnmpResourceType)
            resourceDir = new File("target/snmp/" + nodeId + "/" + resourceName);
        else
            resourceDir = new File("target/snmp/" + nodeId + "/" + resourceType.getName() + "/" + resourceName);
        resourceDir.mkdirs();
        return resourceDir;
    }

}
