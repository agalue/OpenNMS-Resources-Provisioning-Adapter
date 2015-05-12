package org.opennms.netmgt.provision;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opennms.core.test.MockLogAppender;
import org.opennms.netmgt.provision.resourcescanners.DefaultResourceScanner;
import org.opennms.netmgt.provision.resourcescanners.SnmpFrameRelayScanner;

/**
 * The Test Class for ResourceScannerFactory.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class ResourceScannerFactoryTest {

    /**
     * Sets up the test.
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
     * Test getting scanners.
     *
     * @throws Exception the exception
     */
    @Test
    public void testGetScanner() throws Exception {
        Assert.assertTrue(ResourceScannerFactory.getResourceScanner("frCircuitIfIndex") instanceof SnmpFrameRelayScanner);
        Assert.assertTrue(ResourceScannerFactory.getResourceScanner("undefinedResourceType") instanceof DefaultResourceScanner);
    }

}
