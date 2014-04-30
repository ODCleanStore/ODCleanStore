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

    IMPORTING,
    DIRTY,
    PROPAGATED,
    DELETING,
    PROCESSED,
    PROCESSING,
    QUEUED_FOR_DELETE,
    QUEUED_URGENT,
    QUEUED,
    FINISHED,
    WRONG,
    DELETED,
    OLDGRAPHSPREFIXED,
    NEWGRAPHSPREPARED;

    private static final String ERROR_DECODE_GRAPH_STATE = "Error during decoding id to GraphState";

    public int toId() {
        return this.ordinal() + 1;
    }

    public static EnumGraphState fromId(int id) throws ModelException {
        EnumGraphState[] values = EnumGraphState.values();
        if (id < 1 || id > values.length) {
            throw new ModelException(ERROR_DECODE_GRAPH_STATE);
        }
        return EnumGraphState.values()[id - 1];
    }
}
