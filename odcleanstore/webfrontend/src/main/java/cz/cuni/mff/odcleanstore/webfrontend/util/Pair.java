package cz.cuni.mff.odcleanstore.webfrontend.util;

import java.io.Serializable;

/**
 * Represents an ordered pair of variables of types T and U.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final T first;
	private final U second;
	
	/**
	 * 
	 * @param first
	 * @param second
	 */
	public Pair(T first, U second)
	{
		this.first = first;
		this.second = second;
	}

	/**
	 * 
	 * @return
	 */
	public T getFirst() 
	{
		return first;
	}

	/**
	 * 
	 * @return
	 */
	public U getSecond() 
	{
		return second;
	}
}
