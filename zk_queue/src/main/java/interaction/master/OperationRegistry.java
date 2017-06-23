package interaction.master;

import interaction.operation.master.MasterOperation;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class OperationRegistry {
    private final static Logger LOG = Logger.getLogger(OperationRegistry.class);

    private final MasterContext _context;
    private final List<OperationWatchdog> _watchdogs = new ArrayList<OperationWatchdog>();

    public OperationRegistry(MasterContext context) {
        _context = context;
    }

    public synchronized void watchFor(OperationWatchdog watchdog) {
        LOG.info("watch operation '" + watchdog.getOperation() + "' for node operations " + watchdog.getOperationIds());
        releaseDoneWatchdogs(); // lazy cleaning
        _watchdogs.add(watchdog);
        watchdog.start(_context);
    }

    private void releaseDoneWatchdogs() {
        for (Iterator<OperationWatchdog> iterator = _watchdogs.iterator(); iterator.hasNext();) {
            OperationWatchdog watchdog = iterator.next();
            if (watchdog.isDone()) {
                _context.getMasterQueue().removeWatchdog(watchdog);
                iterator.remove();
            }
        }
    }

    public synchronized List<MasterOperation> getRunningOperations() {
        List<MasterOperation> operations = new ArrayList<MasterOperation>();
        for (Iterator<OperationWatchdog> iterator = _watchdogs.iterator(); iterator.hasNext();) {
            OperationWatchdog watchdog = iterator.next();
            if (watchdog.isDone()) {
                iterator.remove(); // lazy cleaning
            } else {
                operations.add(watchdog.getOperation());
            }
        }
        return operations;
    }

    public synchronized void shutdown() {
        for (Iterator<OperationWatchdog> iterator = _watchdogs.iterator(); iterator.hasNext();) {
            OperationWatchdog watchdog = iterator.next();
            watchdog.cancel();
            iterator.remove();
        }
    }
}
