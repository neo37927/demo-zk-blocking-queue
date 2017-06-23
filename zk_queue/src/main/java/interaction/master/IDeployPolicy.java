package interaction.master;

import java.util.List;
import java.util.Map;

/**
 * Exchangeable policy for which creates an distribution plan for the shards of one index.
 *
 * Created by xiaolin  on 2017/4/1.
 */
public interface IDeployPolicy {
    /**
     * Creates a distribution plan for the shards of one index. Note that the
     * index can already be deployed, in that case its more a "replication" plan.
     *
     * @param currentShard2NodesMap
     *          all current deployments of the shards of the one index to
     *          distribute/replicate
     * @param currentNode2ShardsMap
     *          all nodes and their shards
     * @param aliveNodes
     * @param replicationLevel
     * @return
     */
    Map<String, List<String>> createDistributionPlan(Map<String, List<String>> currentShard2NodesMap,
                                                     Map<String, List<String>> currentNode2ShardsMap, List<String> aliveNodes, int replicationLevel);
}
