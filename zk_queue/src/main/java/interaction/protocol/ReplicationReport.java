package interaction.protocol;

import java.util.Map;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class ReplicationReport {
    private final int _desiredReplicationCount;
    private final int _minimalShardReplicationCount;
    private final int _maximalShardReplicationCount;
    private final Map<String, Integer> _shard2ReplicationCount;

    public ReplicationReport(Map<String, Integer> replicationCountByShardMap, int desiredReplicationCount,
                             int minimalShardReplicationCount, int maximalShardReplicationCount) {
        _shard2ReplicationCount = replicationCountByShardMap;
        _desiredReplicationCount = desiredReplicationCount;
        _minimalShardReplicationCount = minimalShardReplicationCount;
        _maximalShardReplicationCount = maximalShardReplicationCount;
    }

    public int getReplicationCount(String shardName) {
        return _shard2ReplicationCount.get(shardName);
    }

    public int getDesiredReplicationCount() {
        return _desiredReplicationCount;
    }

    public int getMinimalShardReplicationCount() {
        return _minimalShardReplicationCount;
    }

    public int getMaximalShardReplicationCount() {
        return _maximalShardReplicationCount;
    }

    public boolean isUnderreplicated() {
        return getMinimalShardReplicationCount() < getDesiredReplicationCount();
    }

    public boolean isOverreplicated() {
        return getMaximalShardReplicationCount() > getDesiredReplicationCount();
    }

    public boolean isBalanced() {
        return !isUnderreplicated() && !isOverreplicated();
    }

    /**
     * @return true if each shard is deployed at least once
     */
    public boolean isDeployed() {
        return getMinimalShardReplicationCount() > 0;
    }

    @Override
    public String toString() {
        return String.format("desiredReplication: %s | minimalShardReplication: %s | maximalShardReplication: %s",
                _desiredReplicationCount, _minimalShardReplicationCount, _maximalShardReplicationCount);
    }

}
