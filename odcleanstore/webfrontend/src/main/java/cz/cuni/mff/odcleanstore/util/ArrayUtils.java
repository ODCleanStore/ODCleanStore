package cz.cuni.mff.odcleanstore.util;

/**
 * Helper methods to ease working with arrays.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ArrayUtils 
{
	/**
	 * Returns a string which contains the elements of the given array
	 * converted to strings and delimited by the given delimiter.
	 * 
	 * @param array
	 * @param delimiter
	 * @return
	 */
	public static <T> String joinArrayItems(T[] array, String delimiter)
	{
		if (array.length == 0)
			return "";
		
		StringBuilder builder = new StringBuilder();
		
		for (T item : array)
		{
			builder.append(item);
			builder.append(delimiter);
		}
		
		String result = builder.toString();
		
		return 
			result.substring(0, result.length() - delimiter.length());	
	}
}
