package org.opennms.netmgt.provision;

import java.util.HashMap;
import java.util.Map;

import org.opennms.netmgt.provision.resourcescanners.DefaultResourceScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * A factory for creating ResourceScanner objects.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class ResourceScannerFactory {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(ResourceScannerFactory.class);

    /** The Constant RESOURCE_SCANNERS_PACKAGE. */
    public static final String RESOURCE_SCANNERS_PACKAGE = DefaultResourceScanner.class.getPackage().getName();

    /** The resource scanners. */
    private static Map<String, String> resourceScanners;

    /** The initialized. */
    private static boolean initialized = false;

    /**
     * Instantiates a new resource scanner factory.
     */
    private ResourceScannerFactory() {}

    /**
     * The initialization method.
     * <p>Must be called at least once.</p>
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        LOG.info("initializing resource scanners.");
        resourceScanners = new HashMap<String, String>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ResourceScannerSource.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(RESOURCE_SCANNERS_PACKAGE)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                String source = clazz.getAnnotation(ResourceScannerSource.class).value();
                LOG.debug("found resource scanner {} for resource type {}",  bd.getBeanClassName(), source);
                resourceScanners.put(source, bd.getBeanClassName());
            } catch (Exception e) {
                LOG.error("cannot initialize resource scanner {} because {}.",  bd.getBeanClassName(), e.getMessage());
            }
        }
    }

    /**
     * Gets the resource scanner.
     *
     * @param resourceType the resource type
     * @return the resource scanner
     * @throws ResourceScannerException the resource scanner exception
     */
    public static ResourceScanner getResourceScanner(String resourceType) throws ResourceScannerException {
        validate();
        String className = resourceScanners.get(resourceType);
        if (className != null) {
            try {
                Class<?> clazz = Class.forName(className);
                LOG.debug("instantiating resource scanner {} for resource type {}.",  className, resourceType);
                return (ResourceScanner)clazz.newInstance();
            } catch (Exception e) {
                LOG.error("Can't instantiate resource scanner {} because {}.",  className, e.getMessage());
            }
        }
        LOG.debug("instantiating default resource scanner for resource type {}.",  resourceType);
        return new DefaultResourceScanner();
    }

    /**
     * Validate.
     *
     * @throws ResourceScannerException the resource scanner exception
     */
    private static void validate() throws ResourceScannerException {
        if (!initialized) {
            throw new ResourceScannerException("ResourceScannerFactory has not been initialized.");
        }
    }

}
