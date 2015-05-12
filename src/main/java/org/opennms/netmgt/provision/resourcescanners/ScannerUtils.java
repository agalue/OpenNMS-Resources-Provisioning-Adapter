package org.opennms.netmgt.provision.resourcescanners;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ScannerUtils.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class ScannerUtils {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(ScannerUtils.class);

    /**
     * Instantiates a new scanner utilities.
     */
    private ScannerUtils() {}

    /**
     * Load properties.
     *
     * @param fileName the file name
     * @return the properties
     */
    public static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(ScannerUtils.class.getResourceAsStream(fileName));
        } catch (Exception e) {
            LOG.error("Can't load {} because{}.", fileName, e.getMessage(), e); 
        }
        return properties;
    }

}
