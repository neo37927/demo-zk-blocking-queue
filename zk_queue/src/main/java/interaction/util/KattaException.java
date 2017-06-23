package interaction.util;

/**
 * Created by xiaolin  on 2017/4/1.
 */

public class KattaException extends Exception {

    private static final long serialVersionUID = 1L;

    public KattaException(String message) {
        super(message);
    }

    public KattaException(final String msg, final Throwable t) {
        super(msg, t);
    }

}