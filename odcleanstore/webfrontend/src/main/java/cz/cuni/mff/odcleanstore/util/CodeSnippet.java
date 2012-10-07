package cz.cuni.mff.odcleanstore.util;

/**
 * A generic code snippet (e.g. a closure). Subclass this class in order
 * to implement a specific closure.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class CodeSnippet 
{
	/**
	 * The code to be executed as the code snippet executes.
	 * 
	 * @throws Exception
	 */
	public abstract void execute() throws Exception;
}
