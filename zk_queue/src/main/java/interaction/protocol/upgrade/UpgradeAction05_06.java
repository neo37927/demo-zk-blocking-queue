package interaction.protocol.upgrade;

import interaction.protocol.InteractionProtocol;
import interaction.protocol.metadata.IndexMetaData;
import interaction.util.ZkConfiguration;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class UpgradeAction05_06 implements UpgradeAction {

    private static final Logger LOG = Logger.getLogger(UpgradeAction05_06.class);

    @SuppressWarnings("deprecation")
    public void upgrade(InteractionProtocol protocol) {
        ZkClient zkClient = protocol.getZkClient();
        ZkConfiguration zkConf = protocol.getZkConfiguration();
        ZkClient zkClientForWriables = new ZkClient(zkConf.getZKServers(), 5000, 5000, new WriteableZkSerializer(
                interaction.index.IndexMetaData.class));
        LOG.info("restoring indices meta data...");
        String oldIndicesPath = getOldIndicesPath(zkConf);
        List<String> indices = zkClient.getChildren(oldIndicesPath);
        LOG.info("found " + indices.size() + " old indices");
        for (String indexName : indices) {
            interaction.index.IndexMetaData oldIndexMD = zkClientForWriables.readData(oldIndicesPath + "/" + indexName);
            IndexMetaData newIndexMD = new IndexMetaData(indexName, oldIndexMD.getPath(), oldIndexMD.getReplicationLevel());
            IndexReinitializeOperation deployOperation = new IndexReinitializeOperation(newIndexMD);
            protocol.addMasterOperation(deployOperation);
            protocol.publishIndex(newIndexMD);
        }
        zkClientForWriables.close();

        LOG.info("deleting obsolete folders...");
        zkClient.deleteRecursive(zkConf.getZkRootPath() + "/" + "indexes");
        zkClient.deleteRecursive(zkConf.getZkRootPath() + "/" + "node-to-shard");
        zkClient.deleteRecursive(zkConf.getZkRootPath() + "/" + "shard-to-node");
        zkClient.deleteRecursive(zkConf.getZkRootPath() + "/" + "loadtest-nodes");
        zkClient.deleteRecursive(zkConf.getZkRootPath() + "/" + "server-metrics");
        zkClient.deleteRecursive(zkConf.getZkRootPath() + "/" + "shard-to-error");

        LOG.info("upgrade done");
    }

    protected static String getOldIndicesPath(ZkConfiguration zkConf) {
        return zkConf.getZkRootPath() + "/" + "indexes";
    }

    static class WriteableZkSerializer implements ZkSerializer {

        private final Class<? extends Writable> _writableClass;

        public WriteableZkSerializer(Class<? extends Writable> writableClass) {
            _writableClass = writableClass;
        }

        public Object deserialize(byte[] bytes) throws ZkMarshallingError {
            DataInputBuffer buffer = new DataInputBuffer();
            buffer.reset(bytes, bytes.length);
            try {
                Writable instance = _writableClass.newInstance();
                instance.readFields(buffer);
                buffer.close();
                return instance;
            } catch (Exception e) {
                throw new ZkMarshallingError(e);
            }
        }

        public byte[] serialize(Object data) throws ZkMarshallingError {
            DataOutputBuffer out = new DataOutputBuffer();
            try {
                ((Writable) data).write(out);
                return out.getData();
            } catch (Exception e) {
                throw new ZkMarshallingError(e);
            } finally {
                IOUtils.closeStream(out);
            }
        }

    }
}

