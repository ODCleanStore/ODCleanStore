package cz.cuni.mff.odcleanstore.connection;

/**
 * Virtuoso logging level.
 * @author Jan Michelfeit
 */
public enum EnumLogLevel {
    /** Disable log. */
    DISABLED(0),
    
    /**
     * Disabled log with enabled row-by-row autocommit. 
     * To be used with SPARUL and RDF data loading. 
     */
    AUTOCOMMIT(2),

    /** Enable transaction level log. */
    TRANSACTION_LEVEL(1),

    /** Enable statement level log and row-by-row autocommit. */
    STATEMENT_LEVEL(3);

    private static final int AUTOCOMMIT_MASK = 0x02;
    
    private int levelBits;

    /**
     * Initialize.
     * @param levelBits bits parameter for Virtuoso log_level() function
     */
    private EnumLogLevel(int levelBits) {
        this.levelBits = levelBits;
    }

    /**
     * Returns the corresponding bits parameter for Virtuoso log_level() function.
     * @return bits parameter for Virtuoso log_level() function
     */
    public int getBits() {
        return levelBits;
    }
    
    /**
     * Returns autocommit settings for the selected transaction log level.
     * @return true if the setting corresponds to row-by-row autocommit mode
     */
    public boolean getAutocommit() {
        return (levelBits & AUTOCOMMIT_MASK) != 0;
    }
}