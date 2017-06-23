package interaction.protocol.metadata;

import interaction.util.DefaultDateFormat;

import java.io.Serializable;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class MasterMetaData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _masterName;
    private long _startTime;

    public MasterMetaData(final String masterName, final long startTime) {
        _masterName = masterName;
        _startTime = startTime;
    }

    public String getStartTimeAsString() {
        return DefaultDateFormat.longToDateString(_startTime);
    }

    public String getMasterName() {
        return _masterName;
    }

    public long getStartTime() {
        return _startTime;
    }

    @Override
    public String toString() {
        return getMasterName() + ":" + getStartTime();
    }
}
