/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.db.model;

/**
 * Contains symbolic names for id of Engine EN_INPUT_GRAPHS_STATES codebook table.
 * 
 * @author Petr Jerman
 */
public enum GraphStates {

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
    FINISHEDINDIRTY,
    REMOVEDOLDGRAPH;

    private static final String ERROR_DECODE_GRAPH_STATE = "Error during decoding id to GraphState";

    public int toId() {
        return this.ordinal() + 1;
    }

    public static GraphStates fromId(int id) throws DbOdcsException {
        GraphStates[] values = GraphStates.values();
        if (id < 1 || id > values.length) {
            throw new DbOdcsException(ERROR_DECODE_GRAPH_STATE);
        }
        return GraphStates.values()[id - 1];
    }
}
