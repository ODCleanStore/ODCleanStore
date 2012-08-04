/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

/**
 *  @author Petr Jerman
 */
public class InputGraphState {
	public static final int IMPORTING = 1;
	public static final int QUEUED_FOR_DELETE = 2;
	public static final int QUEUED_URGENT = 3;
	public static final int QUEUED = 4;
	public static final int DELETING = 5;
	public static final int PROCESSING = 6;
	public static final int PROCESSED = 7;
	public static final int PROPAGATED = 8;
	public static final int FINISHED = 9;
	public static final int DELETED = 10;
	public static final int WRONG = 11;
	public static final int DIRTY = 12;
}
