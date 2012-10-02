package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.util.Locale;

import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.convert.IConverter;

/**
 * Label for boolean values, converting them to a more readable form.
 * @author Jan Michelfeit
 */
public class BooleanLabel extends Label implements IConverter<Boolean>
{
	private static final long serialVersionUID = 1L;
	
	public BooleanLabel(String componentId)
	{
		super(componentId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(Class<C> type)
	{
		if (type == Boolean.class)
		{
			return (IConverter<C>) this;
		}
		return super.getConverter(type);
	}

	public Boolean convertToObject(String value, Locale locale)
	{
		if (Localizer.get().getString(Boolean.TRUE.toString(), null).equals(value))
		{
			return true;
		} 
		else if (Localizer.get().getString(Boolean.FALSE.toString(), null).equals(value))
		{
			return false;
		} 
		return null;
	}

	public String convertToString(Boolean value, Locale locale)
	{
		if (Boolean.TRUE.equals(value) || Boolean.FALSE.equals(value))
		{
			return Localizer.get().getString(value.toString(), null);
		}
		else 
		{
			return null;
		}
	}

}
