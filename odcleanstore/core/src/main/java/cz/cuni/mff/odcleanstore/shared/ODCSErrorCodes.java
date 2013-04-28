package cz.cuni.mff.odcleanstore.shared;

/**
 * Error codes used in ODCleanStore.
 * The purpose of error codes is to identify the place in code where an error occurred
 * and to give users the opportunity to look up errors in the manual.
 *
 * @author Jan Michelfeit
 */
public final class ODCSErrorCodes {
    
    /** Disable constructor for a utility class. */
    private ODCSErrorCodes() {
    }

    public static final int QE_DEFAULT_CONFIG_ERR = 10;
    public static final int QE_DEFAULT_CONFIG_DB_ERR = 11;
    public static final int QE_DEFAULT_CONFIG_MULTIVALUE_ERR = 12;
    public static final int QE_DEFAULT_CONFIG_ES_ERR = 13;
    public static final int QE_DEFAULT_CONFIG_AGGREGATION_ERR = 14; 
    public static final Integer QE_DEFAULT_CONFIG_PREFIX_ERR = 15;
    public static final int QE_INPUT_FORMAT_ERR = 16;
    public static final int QE_INPUT_EMPTY_ERR = 17;
    public static final int QE_DATABASE_ERR = 18;
    public static final int QE_CR_ERR = 19;
    public static final int QE_LABEL_PROPS_EMPTY_ERR = 20;
    public static final int QE_LABEL_PROPS_DB_ERR = 21;
    public static final int QE_NG_METADATA_DB_ERR = 22;
    public static final int QE_PREFIX_MAPPING_UNKNOWN_ERR = 23;
    public static final int QE_PREFIX_MAPPING_DB_ERR = 24;
    public static final int QE_CR_UNKNOWN_RESOLUTION = 25;
    
}
