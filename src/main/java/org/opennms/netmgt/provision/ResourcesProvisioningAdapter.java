package org.opennms.netmgt.provision;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.opennms.netmgt.EventConstants;
import org.opennms.netmgt.dao.api.NodeDao;
import org.opennms.netmgt.dao.api.ResourceDao;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.ResourceTypeUtils;
import org.opennms.netmgt.model.RrdGraphAttribute;
import org.opennms.netmgt.model.events.EventBuilder;
import org.opennms.netmgt.model.events.EventForwarder;
import org.opennms.netmgt.model.events.annotations.EventHandler;
import org.opennms.netmgt.model.events.annotations.EventListener;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Parm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/*
 * TODO: Create a cleanup handler to remove resources that doesn't exist anymore.
 * TODO(DevJam): Add a way to execute multiple visitors for the CollectionSet (to avoid this kind of procedure).
 */
/**
 * The Class ResourcesProvisioningAdapter.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
@EventListener(name=ResourcesProvisioningAdapter.NAME)
public class ResourcesProvisioningAdapter extends SimplerQueuedProvisioningAdapter implements InitializingBean {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesProvisioningAdapter.class);

    /** The Constant NAME. */
    public static final String NAME = "resourcesProvisioningAdapter";

    /** The Constant LAST_UPDATED_PARAM. */
    public static final String LAST_UPDATED_PARAM = "lastUpdated";

    /** The Constant START_RESOURCE_SCAN_UEI. */
    public static final String START_RESOURCE_SCAN_UEI = "uei.opennms.org/internal/provisioning/startResourceScan";

    /** The Constant RESOURCE_SCAN_FINISHED_UEI. */
    public static final String RESOURCE_SCAN_FINISHED_UEI = "uei.opennms.org/internal/provisioning/resourceScanFinished";

    /** The Constant RESOURCE_SCAN_FAILED_UEI. */
    public static final String RESOURCE_SCAN_FAILED_UEI = "uei.opennms.org/internal/provisioning/resourceScanFailed";

    /** The OpenNMS Node DAO. */
    protected NodeDao m_nodeDao;

    /** The OpenNMS Resource DAO. */
    protected ResourceDao m_resourceDao;

    /** The OpenNMS Event Forwarder. */
    protected EventForwarder m_eventForwarder;

    /**
     * Instantiates a new resources provisioning adapter.
     */
    public ResourcesProvisioningAdapter() {
        super(NAME);
    }

    /**
     * Sets the event forwarder.
     *
     * @param eventForwarder the new event forwarder
     */
    public void setEventForwarder(EventForwarder eventForwarder) {
        m_eventForwarder = eventForwarder;
    }

    /**
     * Sets the OpenNMS Resource DAO.
     *
     * @param resourceDao the new OpenNMS Resource DAO
     */
    public void setResourceDao(ResourceDao resourceDao) {
        m_resourceDao = resourceDao;
    }

    /**
     * Sets the OpenNMS Node DAO.
     *
     * @param nodeDao the new OpenNMS Node DAO
     */
    public void setNodeDao(NodeDao nodeDao) {
        m_nodeDao = nodeDao;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(m_nodeDao, "OpenNMS Node DAO cannot be null");
        Assert.notNull(m_resourceDao, "OpenNMS Resource DAO cannot be null");
        Assert.notNull(m_eventForwarder, "OpenNMS Event Forwarder cannot be null");
        ResourceScannerFactory.init();
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.SimplerQueuedProvisioningAdapter#getName()
     */
    @Override
    public String getName() {
        return NAME;
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.SimplerQueuedProvisioningAdapter#isNodeReady(org.opennms.netmgt.provision.SimpleQueuedProvisioningAdapter.AdapterOperation)
     */
    @Override
    public boolean isNodeReady(AdapterOperation op) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.provision.SimplerQueuedProvisioningAdapter#doUpdateNode(int)
     */
    @Override
    public void doUpdateNode(int nodeId) {
        LOG.debug("doUpdateNode: scanning nodeId {}", nodeId);
        // Getting OpenNMS Node
        OnmsNode node = m_nodeDao.get(nodeId);
        if (node == null || node.getIpInterfaces().isEmpty()) {
            sendScanFailed(nodeId, null, new IllegalArgumentException("Node " + nodeId + " does not exist or does not contain any IP address."));
            return;
        }
        // Getting Node's IP Address
        OnmsIpInterface intf = node.getPrimaryInterface();
        if (intf == null)
            intf = node.getIpInterfaces().iterator().next();
        InetAddress ipAddress = intf.getIpAddress();
        // Getting Node's Resource
        String resourceId = OnmsResource.createResourceId("node", Integer.toString(nodeId));
        OnmsResource resource = m_resourceDao.getResourceById(resourceId);
        if (resource == null) {
            sendScanFailed(nodeId, ipAddress, new IllegalArgumentException("Can't build resource for node " + nodeId));
            return;
        }
        // Processing Resources
        final ResourceScannerStats stats = new ResourceScannerStats();
        final Map<String, ResourceScanner> scanners = new HashMap<String, ResourceScanner>();
        try {
            for (OnmsResource childResource : resource.getChildResources()) {
                // Getting Resource Scanner
                String rt = childResource.getResourceType().getName();
                if (scanners.get(rt) == null) {
                    scanners.put(rt, ResourceScannerFactory.getResourceScanner(rt));
                }
                // Process Resource
                try {
                    File resourceDir = getResourceDirectory(childResource);
                    if (resourceDir == null) {
                        LOG.warn("doUpdateNode: can't find resource directory for {}", childResource.getId());
                        stats.incFailedResources();
                        continue;
                    }
                    scanners.get(rt).scanResource(childResource, resourceDir, ipAddress);
                    boolean isNew = childResource.getStringPropertyAttributes().get(LAST_UPDATED_PARAM) == null;
                    if (isNew) {
                        stats.incNewResources();
                    } else {
                        stats.incUpdatedResources();
                    }
                    ResourceTypeUtils.updateStringProperty(resourceDir, new Date().toString(),  LAST_UPDATED_PARAM);
                } catch (Exception e) {
                    LOG.warn("doUpdateNode: can't update resource {}: {}", childResource.getId(), e.getMessage(), e);
                    stats.incFailedResources();
                }
            }
        } catch (Exception e) {
            sendScanFailed(nodeId, ipAddress, e);
            return;            
        }
        sendScanSuccess(nodeId, ipAddress, stats);
    }

    /**
     * Send scan failed.
     *
     * @param nodeId the node id
     * @param ipAddress the IP address
     * @param e the exception that cause the failure
     */
    private void sendScanFailed(int nodeId, InetAddress ipAddress, Exception e) {
        EventBuilder eb = new EventBuilder(RESOURCE_SCAN_FAILED_UEI, "Provisiond." + NAME);
        eb.setNodeid(Long.parseLong(Integer.toString(nodeId)));
        eb.setInterface(ipAddress);
        eb.addParam("reason", e.getMessage());
        LOG.warn("sendScanFailed: scanning node failed because: {}", e.getMessage(), e);
        m_eventForwarder.sendNow(eb.getEvent());
    }

    /**
     * Send scan success.
     *
     * @param nodeId the node id
     * @param ipAddress the IP address
     * @param stats the resource statistics
     */
    private void sendScanSuccess(int nodeId, InetAddress ipAddress, ResourceScannerStats stats) {
        LOG.info("sendScanSuccess: scan was successful. {}", stats);
        EventBuilder eb = new EventBuilder(RESOURCE_SCAN_FINISHED_UEI, "Provisiond." + NAME);
        eb.setNodeid(Long.parseLong(Integer.toString(nodeId)));
        eb.setInterface(ipAddress);
        eb.addParam("newResources", stats.getNewResources());
        eb.addParam("updatedResources", stats.getUpdatedResources());
        eb.addParam("failedResources", stats.getFailedResources());
        m_eventForwarder.sendNow(eb.getEvent());
    }

    /**
     * Handle start resource rescan.
     *
     * @param event the event
     */
    @EventHandler(uei = START_RESOURCE_SCAN_UEI)
    public void handleStartResourceRescan(final Event event) {
        updateNode(Integer.getInteger(Long.toString(event.getNodeid())));
    }

    /**
     * Handle reload configuration event.
     *
     * @param event the event
     */
    @EventHandler(uei = EventConstants.RELOAD_DAEMON_CONFIG_UEI)
    public void handleReloadConfigEvent(final Event event) {
        if (isReloadConfigEventTarget(event)) {
            LOG.debug("handleReloadConfigEvent: reloading resource provisioning adapter configuration.");
        }
    }

    /**
     * Gets the resource directory.
     *
     * @param resource the resource
     * @return the resource directory
     */
    private File getResourceDirectory(OnmsResource resource) {
        Map<String, RrdGraphAttribute> attributes = resource.getRrdGraphAttributes();
        if (attributes.isEmpty()) {
            return null;
        }
        File file = new File(m_resourceDao.getRrdDirectory(), attributes.values().iterator().next().getRrdRelativePath());
        return file.getParentFile();
    }

    /**
     * Checks if is reload configuration event target.
     *
     * @param event the event
     * @return true, if is reload configuration event target
     */
    private boolean isReloadConfigEventTarget(final Event event) {
        boolean isTarget = false;
        for (final Parm parm : event.getParmCollection()) {
            if (EventConstants.PARM_DAEMON_NAME.equals(parm.getParmName()) && ("Provisiond." + NAME).equalsIgnoreCase(parm.getValue().getContent())) {
                isTarget = true;
                break;
            }
        }
        LOG.debug("isReloadConfigEventTarget: Provisiond. {} was target of reload event: {}", NAME, isTarget);
        return isTarget;
    }

}
