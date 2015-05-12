package org.opennms.netmgt.provision;

/**
 * The Class ResourceScannerStats.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class ResourceScannerStats {

    /** The amount of new resources. */
    private int newResources = 0;

    /** The amount of updated resources. */
    private int updatedResources = 0;

    /** The amount of failed resources. */
    private int failedResources = 0;

    /**
     * Gets the amount of new resources.
     *
     * @return the new resources
     */
    public int getNewResources() {
        return newResources;
    }

    /**
     * Gets the amount of updated resources.
     *
     * @return the updated resources
     */
    public int getUpdatedResources() {
        return updatedResources;
    }

    /**
     * Gets the amount of failed resources.
     *
     * @return the failed resources
     */
    public int getFailedResources() {
        return failedResources;
    }

    /**
     * Increments the amount of new resources.
     */
    public void incNewResources() {
        newResources++;
    }

    /**
     * Increments the amount of updated resources.
     */
    public void incUpdatedResources() {
        updatedResources++;
    }

    /**
     * Increments the amount of failed resources.
     */
    public void incFailedResources() {
        failedResources++;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[newResources=" + newResources
                + ", updatedResources=" + updatedResources
                + ", failedResources=" + failedResources + "]";
    }

}
