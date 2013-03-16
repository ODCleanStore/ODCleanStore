package cz.cuni.mff.odcleanstore.data;

/**
 * Version of database tables.
 * @author Jan Michelfeit
 */
public enum TableVersion {
    /** Official version of tables visible for Engine. */
    COMMITTED(""),

    /** Working version of tables visible for author of changes. */
    UNCOMMITTED("_UNCOMMITTED");

    private String suffix;

    private TableVersion(String suffix) {
        this.suffix = suffix;
    }

    /** Returns suffix of the respective set of tables. @return suffix string */
    public String getTableSuffix() {
        return suffix;
    }
}
