package interaction.operation.master;

/**
 * Created by xiaolin  on 2017/4/1.
 */
@SuppressWarnings("serial")
public class IndexReinitializeOperation extends IndexDeployOperation {

    public IndexReinitializeOperation(IndexMetaData indexMD) {
        super(indexMD.getName(), indexMD.getPath(), indexMD.getReplicationLevel());
    }

    @Override
    public List<OperationId> execute(MasterContext context, List<MasterOperation> runningOperations) throws Exception {
        InteractionProtocol protocol = context.getProtocol();
        try {
            _indexMD.getShards().addAll(readShardsFromFs(_indexMD.getName(), _indexMD.getPath()));
            protocol.updateIndexMD(_indexMD);
        } catch (Exception e) {
            ExceptionUtil.rethrowInterruptedException(e);
            handleMasterDeployException(protocol, _indexMD, e);
        }
        return null;
    }

    @Override
    public ExecutionInstruction getExecutionInstruction(List<MasterOperation> runningOperations) throws Exception {
        return ExecutionInstruction.EXECUTE;
    }

    @Override
    public void nodeOperationsComplete(MasterContext context, List<OperationResult> results) throws Exception {
        // nothing todo
    }

}