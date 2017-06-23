package interaction.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaolin  on 2017/4/1.
 */
public class DefaultDateFormat {

    public final static SimpleDateFormat FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    public static String longToDateString(final long timeStamp) {
        return FORMAT.format(new Date(timeStamp));
    }
}
