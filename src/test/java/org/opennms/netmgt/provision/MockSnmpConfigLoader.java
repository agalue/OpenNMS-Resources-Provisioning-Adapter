package org.opennms.netmgt.provision;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * The Class MockSnmpConfigLoader.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class MockSnmpConfigLoader {
    
    /** The Constant SNMP_CONFIG. */
    private static final String SNMP_CONFIG = "<?xml version=\"1.0\"?>" +
            "<snmp-config retry=\"1\" timeout=\"2000\" read-community=\"public\" port=\"1161\" version=\"v2c\"/>";

    /**
     * Gets the input stream.
     *
     * @return the input stream
     */
    public static InputStream getInputStream() {
        return new ByteArrayInputStream(SNMP_CONFIG.getBytes());
    }

}
