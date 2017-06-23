package interaction.master;

import interaction.protocol.ConnectedComponent;
import interaction.protocol.IAddRemoveListener;
import interaction.protocol.InteractionProtocol;
import interaction.operation.OperationId;
import interaction.operation.node.OperationResult;
import interaction.util.ZkConfiguration.PathDef;

import interaction.operation.master.MasterOperation;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class OperationWatchdog implements ConnectedComponent, Serializable {

    private static final long serialVersionUID = 1L;
    protected final static Logger LOG = Logger.getLogger(OperationWatchdog.class);

    private final String _queueElementId;
    private final List<OperationId> _openOperationIds;
    private final List<OperationId> _operationIds;
    private MasterContext _context;
    private final MasterOperation _masterOperation;

    public OperationWatchdog(String queueElementId, MasterOperation masterOperation, List<OperationId> operationIds) {
        _queueElementId = queueElementId;
        _operationIds = operationIds;
        _masterOperation = masterOperation;
        _openOperationIds = new ArrayList<OperationId>(operationIds);
    }

    public void start(MasterContext context) {
        _context = context;
        subscribeNotifications();
    }

    private final synchronized void subscribeNotifications() {
        checkDeploymentForCompletion();
        if (isDone()) {
            return;
        }

        InteractionProtocol protocol = _context.getProtocol();
        protocol.registerChildListener(this, PathDef.NODES_LIVE, new IAddRemoveListener() {
            public void removed(String name) {
                checkDeploymentForCompletion();
            }

            public void added(String name) {
                // nothing todo
            }
        });
        IZkDataListener dataListener = new IZkDataListener() {
            public void handleDataDeleted(String arg0) throws Exception {
                checkDeploymentForCompletion();
            }

            public void handleDataChange(String arg0, Object arg1) throws Exception {
                // nothing todo
            }
        };
        for (OperationId operationId : _openOperationIds) {
            protocol.registerNodeOperationListener(this, operationId, dataListener);
        }
        checkDeploymentForCompletion();
    }

    protected final synchronized void checkDeploymentForCompletion() {
        if (isDone()) {
            return;
        }

        List<String> liveNodes = _context.getProtocol().getLiveNodes();
        for (Iterator<OperationId> iter = _openOperationIds.iterator(); iter.hasNext();) {
            OperationId operationId = iter.next();
            if (!_context.getProtocol().isNodeOperationQueued(operationId) || !liveNodes.contains(operationId.getNodeName())) {
                iter.remove();
            }
        }
        if (isDone()) {
            finishWatchdog();
        } else {
            LOG.debug("still " + getOpenOperationCount() + " open deploy operations");
        }
    }

    public synchronized void cancel() {
        _context.getProtocol().unregisterComponent(this);
        this.notifyAll();
    }

    private synchronized void finishWatchdog() {
        InteractionProtocol protocol = _context.getProtocol();
        protocol.unregisterComponent(this);
        try {
            List<OperationResult> operationResults = new ArrayList<OperationResult>(_openOperationIds.size());
            for (OperationId operationId : _operationIds) {
                OperationResult operationResult = protocol.getNodeOperationResult(operationId, true);
                if (operationResult != null && operationResult.getUnhandledException() != null) {
                    // TODO jz: do we need to inform the master operation ?
                    LOG.error("received unhandlde exception from node " + operationId.getNodeName(), operationResult
                            .getUnhandledException());
                }
                operationResults.add(operationResult);// we add null ones
            }
            _masterOperation.nodeOperationsComplete(_context, operationResults);
        } catch (Exception e) {
            LOG.info("operation complete action of " + _masterOperation + " failed", e);
        }
        LOG.info("watch for " + _masterOperation + " finished");
        this.notifyAll();
        _context.getMasterQueue().removeWatchdog(this);
    }

    public String getQueueElementId() {
        return _queueElementId;
    }

    public MasterOperation getOperation() {
        return _masterOperation;
    }

    public List<OperationId> getOperationIds() {
        return _operationIds;
    }

    public final int getOpenOperationCount() {
        return _openOperationIds.size();
    }

    public boolean isDone() {
        return _openOperationIds.isEmpty();
    }

    public final synchronized void join() throws InterruptedException {
        join(0);
    }

    public final synchronized void join(long timeout) throws InterruptedException {
        if (!isDone()) {
            this.wait(timeout);
        }
    }

    public final void disconnect() {
        // handled by master
    }

    public final void reconnect() {
        // handled by master
    }

}
