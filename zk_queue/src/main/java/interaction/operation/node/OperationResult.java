package interaction.operation.node;

import java.io.Serializable;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class OperationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String _nodeName;
    private final Exception _unhandledException;

    public OperationResult(String nodeName) {
        this(nodeName, null);
    }

    public OperationResult(String nodeName, Exception unhandledException) {
        _nodeName = nodeName;
        _unhandledException = unhandledException;
    }

    public String getNodeName() {
        return _nodeName;
    }

    public Exception getUnhandledException() {
        return _unhandledException;
    }

}
