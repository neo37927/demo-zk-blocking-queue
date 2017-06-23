package interaction.protocol;

import interaction.master.OperationWatchdog;
import interaction.operation.OperationId;
import interaction.operation.master.MasterOperation;
import interaction.protocol.BlockingQueue;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class MasterQueue extends BlockingQueue<MasterOperation> {

    private String _watchdogsPath;

    public MasterQueue(ZkClient zkClient, String rootPath) {
        super(zkClient, rootPath);
        _watchdogsPath = rootPath + "/watchdogs";
        _zkClient.createPersistent(_watchdogsPath, true);

        // cleanup odd watchdog situations
        List<String> watchdogs = _zkClient.getChildren(_watchdogsPath);
        for (String elementName : watchdogs) {
            try {
                _zkClient.delete(getElementPath(elementName));
            } catch (ZkNoNodeException e) {
                // ignore, can be already deleted by other queue instance
            }
        }
    }

    private String getWatchdogPath(String elementId) {
        return _watchdogsPath + "/" + elementId;
    }

    /**
     * Moves the top of the queue to the watching state.
     *
     * @param masterOperation
     * @param nodeOperationIds
     * @return
     * @throws InterruptedException
     */
    public OperationWatchdog moveOperationToWatching(MasterOperation masterOperation, List<OperationId> nodeOperationIds)
            throws InterruptedException {
        Element<MasterOperation> element = getFirstElement();
        // we don't use the persisted operation cause the given masterOperation can
        // have a changed state
        OperationWatchdog watchdog = new OperationWatchdog(element.getName(), masterOperation, nodeOperationIds);
        _zkClient.createPersistent(getWatchdogPath(element.getName()), watchdog);
        _zkClient.delete(getElementPath(element.getName()));
        return watchdog;
    }

    public List<OperationWatchdog> getWatchdogs() {
        List<String> childs = _zkClient.getChildren(_watchdogsPath);
        List<OperationWatchdog> watchdogs = new ArrayList<OperationWatchdog>(childs.size());
        for (String child : childs) {
            watchdogs.add((OperationWatchdog) _zkClient.readData(getWatchdogPath(child)));
        }
        return watchdogs;
    }

    public void removeWatchdog(OperationWatchdog watchdog) {
        _zkClient.delete(getWatchdogPath(watchdog.getQueueElementId()));
    }

}
