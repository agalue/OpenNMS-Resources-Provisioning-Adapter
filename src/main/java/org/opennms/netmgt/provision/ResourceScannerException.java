package org.opennms.netmgt.provision;

/**
 * The Class ResourceScannerException.
 *
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class ResourceScannerException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5598588209179859704L;

    /**
     * Instantiates a new resource scanner exception.
     */
    public ResourceScannerException() {
        super();
    }

    /**
     * Instantiates a new resource scanner exception.
     *
     * @param reason the reason
     */
    public ResourceScannerException(final String reason) {
        super(reason);
    }

    /**
     * Instantiates a new resource scanner exception.
     *
     * @param format the message format
     * @param args the message arguments
     */
    public ResourceScannerException(final String format, final Object... args) {
        super((args == null || args.length < 1) ? format : String.format(format, args));
    }

    /**
     * Instantiates a new resource scanner exception.
     *
     * @param reason the reason
     * @param ex the exception
     */
    public ResourceScannerException(final String reason, final Throwable ex) {
        super(reason, ex);
    }

    /**
     * Instantiates a new resource scanner exception.
     *
     * @param ex the exception
     * @param format the message format
     * @param args the message arguments
     */
    public ResourceScannerException(final Throwable ex, final String format, final Object... args) {
        super((args == null || args.length < 1) ? format : String.format(format, args), ex);
    }

    /**
     * Instantiates a new resource scanner exception.
     *
     * @param reason the reason
     */
    public ResourceScannerException(Throwable reason) {
        super(reason);
    }

}
