package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.convert.IConverter;

/**
 * Label for timestamp values, converting them to a more readable form.
 * @author Jan Michelfeit
 */
public class TimestampLabel extends Label implements IConverter<Timestamp>
{
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat dateFormat;
	
	public TimestampLabel(String componentId)
	{
		super(componentId);
		dateFormat = new SimpleDateFormat(Localizer.get().getString("dateFormat", null));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(Class<C> type)
	{
		if (type == Timestamp.class)
		{
			return (IConverter<C>) this;
		}
		return super.getConverter(type);
	}

	public Timestamp convertToObject(String value, Locale locale)
	{
		return null;
	}

	public String convertToString(Timestamp value, Locale locale)
	{
		return dateFormat.format(value);
	}

}
