package interaction.protocol.metadata;

import interaction.util.DefaultDateFormat;

import java.io.Serializable;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class NodeMetaData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _name;
    private float _queriesPerMinute = 0f;
    private long _startTimeStamp = System.currentTimeMillis();

    // with node execution

    public NodeMetaData() {
        // for serialization
    }

    public NodeMetaData(final String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public String getStartTimeAsDate() {
        return DefaultDateFormat.longToDateString(_startTimeStamp);
    }

    public float getQueriesPerMinute() {
        return _queriesPerMinute;
    }

    public void setQueriesPerMinute(final float queriesPerMinute) {
        _queriesPerMinute = queriesPerMinute;
    }

    @Override
    public String toString() {
        return getName() + "\t:\t" + getStartTimeAsDate() + "";
    }

}
