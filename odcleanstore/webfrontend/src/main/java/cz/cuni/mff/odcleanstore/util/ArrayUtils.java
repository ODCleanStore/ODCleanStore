package cz.cuni.mff.odcleanstore.util;

public class ArrayUtils 
{
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
