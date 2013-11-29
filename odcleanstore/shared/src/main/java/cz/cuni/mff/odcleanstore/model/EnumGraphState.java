/**
 * 
 */
package cz.cuni.mff.odcleanstore.model;

import cz.cuni.mff.odcleanstore.connection.exceptions.ModelException;

/**
 * Contains symbolic names for id of Engine EN_INPUT_GRAPHS_STATES codebook table.
 * 
 * @author Petr Jerman
 */
public enum EnumGraphState {
    /** Importing. */
    IMPORTING,
    
    /** Dirty. */
    DIRTY,
    
    /** Propagated. */
    PROPAGATED,
    
    /** Deleting. */
    DELETING,
    
    /** Processed. */
    PROCESSED,
    
    /** Processing. */
    PROCESSING,
    
    /** Queue for deletion. */
    QUEUED_FOR_DELETE,
    
    /** Queued with high priority. */
    QUEUED_URGENT,
    
    /** Queued. */
    QUEUED,
    
    /** Finished. */
    FINISHED,

    /** Wrong (fault) state.. */
    WRONG,
    
    /** Deleted. */
    DELETED,
    
    /** Prefixed with temporary prefix for graphs to be removed. */
    OLDGRAPHSPREFIXED,
    
    /** Prefixed with temporary prefix for new graphs. */
    NEWGRAPHSPREPARED;

    private static final String ERROR_DECODE_GRAPH_STATE = "Error during decoding id to GraphState";

    /**
     * Converts  enum constant to numeric ID.
     * @return ID corresponding to the enum constant.
     */
    public int toId() {
        return this.ordinal() + 1;
    }

    /**
     * Converts numeric ID to enum constant.
     * @param id ID to convert
     * @return corresponding enum constant
     * @throws ModelException invalid id
     */
    public static EnumGraphState fromId(int id) throws ModelException {
        EnumGraphState[] values = EnumGraphState.values();
        if (id < 1 || id > values.length) {
            throw new ModelException(ERROR_DECODE_GRAPH_STATE);
        }
        return EnumGraphState.values()[id - 1];
    }
}
