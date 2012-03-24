package cz.cuni.mff.odcleanstore.queryexecution;

/**
 * @author Jan Michelfeit
 */
public abstract class QueryExecutor {

    private QueryConstraintSpec queryConstraints;

    public QueryExecutor(QueryConstraintSpec queryConstraints) {
        this.queryConstraints = queryConstraints;
    }
}
