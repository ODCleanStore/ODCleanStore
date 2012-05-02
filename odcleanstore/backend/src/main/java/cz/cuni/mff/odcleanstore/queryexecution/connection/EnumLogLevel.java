package cz.cuni.mff.odcleanstore.queryexecution.connection;

/**
 * Virtuoso logging level.
 * @author Jan Michelfeit
 */
public enum EnumLogLevel {
    /** Disable log. */
    DISABLED(0),

    /** Enable transaction level log. */
    TRANSACTION_LEVEL(1),

    /** Enable statement level log. */
    STATEMENT_LEVEL(3);

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
}