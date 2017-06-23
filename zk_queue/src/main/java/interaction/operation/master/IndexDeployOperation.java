package interaction.operation.master;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class IndexDeployOperation extends AbstractIndexOperation {

    private static final long serialVersionUID = 1L;
    private final static Logger LOG = Logger.getLogger(AbstractIndexOperation.class);

    protected IndexMetaData _indexMD;
    private final String _indexName;
    private final String _indexPath;

    public IndexDeployOperation(String indexName, String indexPath, int replicationLevel) {
        _indexMD = new IndexMetaData(indexName, indexPath, replicationLevel);
        _indexName = indexName;
        _indexPath = indexPath;
    }

    public String getIndexName() {
        return _indexName;
    }

    public String getIndexPath() {
        return _indexPath;
    }

    public int getReplicationLevel() {
        return _indexMD.getReplicationLevel();
    }

    @Override
    public List<OperationId> execute(MasterContext context, List<MasterOperation> runningOperations) throws Exception,
            InterruptedException {
        InteractionProtocol protocol = context.getProtocol();
        try {
            _indexMD.getShards().addAll(readShardsFromFs(_indexName, _indexPath));
            LOG.info("Found shards '" + _indexMD.getShards() + "' for index '" + _indexName + "'");
            List<OperationId> operationIds = distributeIndexShards(context, _indexMD, protocol.getLiveNodes(),
                    runningOperations);
            return operationIds;
        } catch (Exception e) {
            ExceptionUtil.rethrowInterruptedException(e);
            LOG.error("failed to deploy index " + _indexName, e);
            protocol.publishIndex(_indexMD);
            handleMasterDeployException(protocol, _indexMD, e);
            return null;
        }
    }

    @Override
    public void nodeOperationsComplete(MasterContext context, List<OperationResult> results) throws Exception {
        LOG.info("deployment of index " + _indexName + " complete");
        handleDeploymentComplete(context, results, _indexMD, true);
    }

    @Override
    public ExecutionInstruction getExecutionInstruction(List<MasterOperation> runningOperations) throws Exception {
        for (MasterOperation operation : runningOperations) {
            if (operation instanceof IndexDeployOperation && ((IndexDeployOperation) operation)._indexName.equals(_indexName)) {
                return ExecutionInstruction.CANCEL;
            }
        }
        return ExecutionInstruction.EXECUTE;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + Integer.toHexString(hashCode()) + ":" + _indexName;
    }

    protected static List<Shard> readShardsFromFs(final String indexName, final String indexPathString)
            throws IndexDeployException {
        // get shard folders from source
        URI uri;
        try {
            uri = new URI(indexPathString);
        } catch (final URISyntaxException e) {
            throw new IndexDeployException(ErrorType.INDEX_NOT_ACCESSIBLE, "unable to parse index path uri '"
                    + indexPathString + "', make sure it starts with file:// or hdfs:// ", e);
        }
        FileSystem fileSystem;
        try {
            fileSystem = HadoopUtil.getFileSystem(new Path(uri.toString()));
        } catch (final IOException e) {
            throw new IndexDeployException(ErrorType.INDEX_NOT_ACCESSIBLE, "unable to retrive file system for index path '"
                    + indexPathString + "', make sure your path starts with hadoop support prefix like file:// or hdfs://", e);
        }

        List<Shard> shards = new ArrayList<Shard>();
        try {
            final Path indexPath = new Path(indexPathString);
            if (!fileSystem.exists(indexPath)) {
                throw new IndexDeployException(ErrorType.INDEX_NOT_ACCESSIBLE, "index path '" + uri + "' does not exists");
            }
            final FileStatus[] listStatus = fileSystem.listStatus(indexPath, new PathFilter() {
                public boolean accept(final Path aPath) {
                    return !aPath.getName().startsWith(".");
                }
            });
            for (final FileStatus fileStatus : listStatus) {
                String shardPath = fileStatus.getPath().toString();
                if (fileStatus.isDir() || shardPath.endsWith(".zip")) {
                    shards.add(new Shard(createShardName(indexName, shardPath), shardPath));
                }
            }
        } catch (final IOException e) {
            throw new IndexDeployException(ErrorType.INDEX_NOT_ACCESSIBLE, "could not access index path: " + indexPathString,
                    e);
        }

        if (shards.size() == 0) {
            throw new IndexDeployException(ErrorType.INDEX_NOT_ACCESSIBLE, "index does not contain any shard");
        }
        return shards;
    }

}
