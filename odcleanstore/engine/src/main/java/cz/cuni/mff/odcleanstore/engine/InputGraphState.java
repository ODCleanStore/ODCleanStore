/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

/**
 *  @author Petr Jerman
 */
public class InputGraphState {
	public static final int IMPORTING = 1;
	public static final int IMPORTED = 2;
	public static final int PROCESSING = 3;
	public static final int PROCESSED = 4;
	public static final int PROPAGATED = 5;
	public static final int FINISHED = 6;
	public static final int DELETING = 7;
	public static final int DIRTY = 8;
	public static final int WRONG = 9;
	public static final int REPAIRED = 10;
}
