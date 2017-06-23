package interaction.operation;

import java.io.Serializable;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class OperationId implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String _nodeName;
    private final String _elementName;

    public OperationId(String nodeName, String elementName) {
        _nodeName = nodeName;
        _elementName = elementName;
    }

    public String getNodeName() {
        return _nodeName;
    }

    public String getElementName() {
        return _elementName;
    }

    @Override
    public String toString() {
        return _nodeName + "-" + _elementName;
    }

}
