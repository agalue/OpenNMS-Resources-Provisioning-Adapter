package org.opennms.netmgt.provision;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface ResourceScannerSource.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceScannerSource {

    /**
     * The resource type name (defined in datacollection-config.xml).
     *
     * @return the string
     */
    String value();

}
