package interaction.protocol.upgrade;

import interaction.protocol.InteractionProtocol;
import interaction.protocol.metadata.Version;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class UpgradeRegistry {

    private static final Logger LOG = Logger.getLogger(UpgradeRegistry.class);
    private static Map<VersionPair, UpgradeAction> _upgradeActionsByVersion = new HashMap<VersionPair, UpgradeAction>();

    static {
        registerUpgradeAction("0.5", "0.6", new UpgradeAction05_06());
    }

    protected static void registerUpgradeAction(String fromVersion, String toVersion, UpgradeAction upgradeAction) {
        _upgradeActionsByVersion.put(new VersionPair(fromVersion, toVersion), upgradeAction);
    }

    public static UpgradeAction findUpgradeAction(InteractionProtocol protocol, Version distributionVersion) {
        Version clusterVersion = protocol.getVersion();
        if (clusterVersion == null) {
            // version exist up from 0.6 only
            boolean isPre0_6Cluster = protocol.getZkClient().exists(
                    protocol.getZkConfiguration().getZkRootPath() + "/indexes");
            if (isPre0_6Cluster) {
                LOG.info("version of cluster not found - assuming 0.5");
                clusterVersion = new Version("0.5", "Unknown", "Unknown", "Unknown");
            } else {
                clusterVersion = distributionVersion;
            }
        }
        LOG.info("version of distribution " + distributionVersion.getNumber());
        LOG.info("version of cluster " + clusterVersion.getNumber());
        if (clusterVersion.equals(distributionVersion)) {
            return null;
        }

        VersionPair currentVersionPair = new VersionPair(clusterVersion.getNumber(), distributionVersion.getNumber());
        LOG.warn("cluster version differs from distribution version " + currentVersionPair);
        for (VersionPair versionPair : _upgradeActionsByVersion.keySet()) {
            LOG.info("checking upgrade action " + versionPair);
            if (currentVersionPair.getFromVersion().startsWith(versionPair.getFromVersion())
                    && currentVersionPair.getToVersion().startsWith(versionPair.getToVersion())) {
                LOG.info("found matching upgrade action");
                return _upgradeActionsByVersion.get(versionPair);
            }
        }

        LOG.warn("found no upgrade action for " + currentVersionPair + " out of " + _upgradeActionsByVersion.keySet());
        return null;
    }

    protected static class VersionPair {
        private String _fromVersion;
        private String _toVersion;

        protected VersionPair(String fromVersion, String toVersion) {
            _fromVersion = fromVersion;
            _toVersion = toVersion;
        }

        public String getFromVersion() {
            return _fromVersion;
        }

        public String getToVersion() {
            return _toVersion;
        }

        @Override
        public String toString() {
            return getFromVersion() + " -> " + getToVersion();
        }
    }

}
