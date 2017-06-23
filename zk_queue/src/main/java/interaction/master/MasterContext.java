package interaction.master;

import interaction.protocol.InteractionProtocol;
import interaction.protocol.MasterQueue;
import interaction.protocol.metadata.IndexMetaData;

import java.io.IOException;

import interaction.util.HadoopUtil;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class MasterContext {
    private final Master _master;
    private final InteractionProtocol _protocol;
    private final IDeployPolicy _deployPolicy;
    private final MasterQueue _masterQueue;

    public MasterContext(InteractionProtocol protocol, Master master, IDeployPolicy deployPolicy, MasterQueue masterQueue) {
        _protocol = protocol;
        _master = master;
        _deployPolicy = deployPolicy;
        _masterQueue = masterQueue;
    }

    public InteractionProtocol getProtocol() {
        return _protocol;
    }

    public Master getMaster() {
        return _master;
    }

    public IDeployPolicy getDeployPolicy() {
        return _deployPolicy;
    }

    public MasterQueue getMasterQueue() {
        return _masterQueue;
    }

    public FileSystem getFileSystem(IndexMetaData indexMd) throws IOException {
        return HadoopUtil.getFileSystem(new Path(indexMd.getPath()));
    }
}
