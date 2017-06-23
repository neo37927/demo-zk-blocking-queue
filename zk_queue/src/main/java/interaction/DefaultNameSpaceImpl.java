package interaction;


import interaction.util.ZkConfiguration;
import interaction.util.ZkConfiguration.PathDef;
import org.I0Itec.zkclient.IDefaultNameSpace;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.log4j.Logger;

/**
 * Implements the default name space in zookeeper for this katta instance.
 */
public class DefaultNameSpaceImpl implements IDefaultNameSpace {

    private static final Logger LOG = Logger.getLogger(DefaultNameSpaceImpl.class);

    private ZkConfiguration _conf;

    public DefaultNameSpaceImpl(ZkConfiguration conf) {
        _conf = conf;
    }

    public void createDefaultNameSpace(ZkClient zkClient) {
        LOG.debug("Creating default File structure if required....");
        safeCreate(zkClient, _conf.getZkRootPath());
        PathDef[] values = PathDef.values();
        for (PathDef pathDef : values) {
            if (pathDef != PathDef.MASTER && pathDef != PathDef.VERSION) {
                safeCreate(zkClient, _conf.getZkPath(pathDef));
            }
        }
    }

    private void safeCreate(ZkClient zkClient, String path) {
        try {
            // first create parent directories
            String parent =ZkConfiguration.getZkParent(path);
            if (parent != null && !zkClient.exists(parent)) {
                safeCreate(zkClient, parent);
            }

            zkClient.createPersistent(path);
        } catch (ZkNodeExistsException e) {
            // Ignore if the node already exists.
        }
    }
}

