package org.opennms.netmgt.provision;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennms.core.test.MockLogAppender;
import org.opennms.core.test.snmp.JUnitSnmpAgentExecutionListener;
import org.opennms.core.test.snmp.annotations.JUnitSnmpAgent;
import org.opennms.netmgt.model.events.EventUtils;
import org.opennms.netmgt.dao.api.NodeDao;
import org.opennms.netmgt.dao.api.ResourceDao;
import org.opennms.netmgt.dao.support.InterfaceSnmpResourceType;
import org.opennms.netmgt.dao.support.NodeResourceType;
import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.NetworkBuilder;
import org.opennms.netmgt.model.OnmsAttribute;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.RrdGraphAttribute;
import org.opennms.netmgt.xml.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * The Test Class for ResourcesProvisioningAdapter.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
    JUnitSnmpAgentExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class
})
@ContextConfiguration(locations= {
        "classpath:/META-INF/opennms/applicationContext-proxy-snmp.xml",
        "classpath:/META-INF/opennms/provisiond-extensions-test.xml",
        "classpath:/META-INF/opennms/provisiond-extensions.xml"
})
public class ResourcesProvisioningAdapterTest {


    /** The Resources Provisioning Adapter. */
    @Autowired
    private ResourcesProvisioningAdapter adapter;

    /** The OpenNMS Resource DAO. */
    @Autowired
    private ResourceDao resourceDao;

    /** The OpenNMS Node DAO. */
    @Autowired
    private NodeDao nodeDao;

    /** The Mock Event Forwarder. */
    @Autowired
    private MockEventForwarder eventForwarder;

    /** The resource directory. */
    private File resourceDir;

    /**
     * Sets up the test.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        MockLogAppender.setupLogging();

        File snmpDir = new File("target/snmp");
        resourceDir = new File(snmpDir, "1/en0");
        resourceDir.mkdirs();

        Set<OnmsAttribute> attribtues = new HashSet<OnmsAttribute>();
        attribtues.add(new RrdGraphAttribute("ifInOctets", "1/en0", "ifInOctets.jrb"));
        attribtues.add(new RrdGraphAttribute("ifOutOctets", "1/en0", "ifOutOctets.jrb"));

        OnmsResource eth0 = new OnmsResource("en0", "Ethernet 0", new InterfaceSnmpResourceType(resourceDao, null), attribtues);
        OnmsResource node = new OnmsResource("1", "node", new NodeResourceType(resourceDao), attribtues, Collections.singletonList(eth0));
        eth0.setParent(node);

        NetworkBuilder builder = new NetworkBuilder();
        builder.addNode("junit-node");
        builder.addSnmpInterface(1).addIpInterface("192.168.1.1");

        EasyMock.expect(nodeDao.get(1)).andReturn(builder.getCurrentNode()).anyTimes();
        Properties properties = new Properties();
        properties.store(new FileOutputStream(new File(resourceDir, "strings.properties")), "JUnit test");
        EasyMock.expect(resourceDao.getRrdDirectory()).andReturn(snmpDir).anyTimes();
        EasyMock.expect(resourceDao.getResourceById("node[1]")).andReturn(node).anyTimes();
        EasyMock.replay(resourceDao, nodeDao);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
        EasyMock.verify(resourceDao, nodeDao);
        FileUtils.deleteDirectory(resourceDir);
        MockLogAppender.assertNoWarningsOrGreater();
    }

    /**
     * Test adapter.
     *
     * @throws Exception the exception
     */
    @Test
    @JUnitSnmpAgent(host="192.168.1.1", port=1161, resource = "classpath:macos.properties")
    public void testAdapter() throws Exception {
        Assert.assertNotNull(adapter);
        adapter.doUpdateNode(1);
        Assert.assertNotNull(ResourceTypeUtils.getStringProperty(resourceDir, ResourcesProvisioningAdapter.LAST_UPDATED_PARAM));
        List<Event> events = eventForwarder.getEvents();
        Assert.assertEquals(1, events.size());
        Event e = events.get(0);
        Assert.assertEquals("1", EventUtils.getParm(e, "newResources"));
        Assert.assertEquals("0", EventUtils.getParm(e, "updatedResources"));
        Assert.assertEquals("0", EventUtils.getParm(e, "failedResources"));
    }

}
