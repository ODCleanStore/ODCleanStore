package cz.cuni.mff.odcleanstore.webfrontend.util;

/**
 * A generic code snippet (e.g. a closure). Subclass this class in order
 * to implement a specific closure (e.g. using an anonymous inner class).
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
