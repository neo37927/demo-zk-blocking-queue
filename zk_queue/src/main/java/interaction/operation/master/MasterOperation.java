package interaction.operation.master;

import interaction.master.MasterContext;
import interaction.operation.OperationId;
import interaction.operation.node.OperationResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public interface MasterOperation extends Serializable {

    static enum ExecutionInstruction {
        EXECUTE, CANCEL, ADD_TO_QUEUE_TAIL;
    }

    /**
     * Called before {@link #execute(MasterContext, List)} to evaluate if this
     * operation is blocked, delayed, etc by another running
     * {@link MasterOperation}.
     *
     * @param runningOperations
     * @return instruction
     * @throws Exception
     */
    ExecutionInstruction getExecutionInstruction(List<MasterOperation> runningOperations) throws Exception;

    /**
     * @param context
     * @param runningOperations
     *          currently running {@link MasterOperation}s
     * @return null or a list of operationId which have to be completed before
     *         {@link #nodeOperationsComplete(MasterContext)} method is called.
     * @throws Exception
     */
    List<OperationId> execute(MasterContext context, List<MasterOperation> runningOperations) throws Exception;

    /**
     * Called when all operations are complete or the nodes of the incomplete
     * operations went down. This method is NOT called if
     * {@link #execute(MasterContext)} returns null or an emptu list of
     * {@link OperationId}s.
     *
     * @param context
     * @param results
     */
    void nodeOperationsComplete(MasterContext context, List<OperationResult> results) throws Exception;

}
