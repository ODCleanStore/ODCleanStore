package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.util.Locale;

import cz.cuni.mff.odcleanstore.model.EnumGraphState;

/**
 * Label for {@link EnumGraphState} values, converting them to a more readable form.
 * @author Jan Michelfeit
 */
public class StateLabel extends StringifiedEnumLabel
{
	private static final long serialVersionUID = 1L;
	
	public StateLabel(String componentId)
	{
		super(componentId);
	}
	
	@Override
	public String convertToString(String value, Locale locale)
	{
		if (value.equals(EnumGraphState.PROPAGATED.name()) 
			|| value.equals(EnumGraphState.OLDGRAPHSPREFIXED.name())
			|| value.equals(EnumGraphState.NEWGRAPHSPREPARED.name())) {
			
			return "Propagated";
		} else {
			return super.convertToString(value, locale);
		}
	}
}
