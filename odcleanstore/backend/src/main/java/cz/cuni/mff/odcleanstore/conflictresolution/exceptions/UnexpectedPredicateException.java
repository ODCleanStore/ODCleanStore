package cz.cuni.mff.odcleanstore.conflictresolution.exceptions;

/**
 * Exception to throw when a triple is used with a predicate different from
 * what is expected.
 *
 * @author Jan Michelfeit
 */
public class UnexpectedPredicateException extends ConflictResolutionException {
    /**
     *  Initialize exception.
     *  @param actualPredicate predicate given
     *  @param expectedPredicate expected predicate
     */
    public UnexpectedPredicateException(String actualPredicate, String expectedPredicate) {
        super("Unexpected predicate "
                + actualPredicate
                + " in triple ("
                + expectedPredicate
                + " expected)");
    }
}
