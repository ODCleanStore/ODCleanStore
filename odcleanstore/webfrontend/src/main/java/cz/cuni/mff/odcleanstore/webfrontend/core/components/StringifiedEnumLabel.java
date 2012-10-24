package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.convert.IConverter;

/**
 * Label for values created from enum values converted to string.
 * @author Jan Michelfeit
 */
public class StringifiedEnumLabel extends Label implements IConverter<String>
{
	private static final long serialVersionUID = 1L;

	public StringifiedEnumLabel(String componentId)
	{
		super(componentId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(Class<C> type)
	{
		if (type == String.class)
		{
			return (IConverter<C>) this;
		}
		return super.getConverter(type);
	}

	public String convertToObject(String value, Locale locale)
	{
		return value.replace(' ', '_').toUpperCase();
	}

	public String convertToString(String value, Locale locale)
	{
		return convertToReadable(value.toString(), locale);
	}

	protected static String convertToReadable(String s, Locale locale)
	{
		if (s.length() <= 1)
		{
			return s.toUpperCase();
		}
		else
		{
			String result = Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
			result = result.replace('_', ' ');
			return result;
		}
	}

}
