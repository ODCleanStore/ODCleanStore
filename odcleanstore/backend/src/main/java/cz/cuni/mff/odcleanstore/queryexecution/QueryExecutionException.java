package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Exception during Query Execution.
 * @author Jan Michelfeit
 */
public class QueryExecutionException extends ODCleanStoreException {
    private static final long serialVersionUID = 3420323334894817996L;

    private final EnumQueryError errorType;

    private final Integer errorCode;

    /**
     * Constructs a new exception with the given cause.
     * @param errorType type of the error
     * @param errorCode code of the error
     * @param cause the cause
     */
    public QueryExecutionException(EnumQueryError errorType, Integer errorCode, Throwable cause) {
        super(cause);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new exception with the given message and cause.
     * @param errorType type of the error
     * @param errorCode code of the error
     * @param message the detail message
     * @param cause the cause
     */
    public QueryExecutionException(EnumQueryError errorType, Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new exception with the given message.
     * @param errorType type of the error
     * @param errorCode code of the error
     * @param message the detail message
     */
    public QueryExecutionException(EnumQueryError errorType, Integer errorCode, String message) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    /**
     * Return the type of the error.
     * Can be used to generate more user-friendly error messages.
     * @return type of the error
     */
    public EnumQueryError getErrorType() {
        return errorType;
    }

    /**
     * Return the error code of the error.
     * @see cz.cuni.mff.odcleanstore.shared.util.ODCSErrorCodes
     * @return error code or null
     */
    public Integer getErrorCode() {
        return errorCode;
    }
}
