package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class PropertySettings extends BusinessObject 
{
	private static final long serialVersionUID = 1L;

	private String property;
	private boolean multivalue;
	private AggregationType aggregationType;
	
	public PropertySettings(Long id, String property, boolean multivalue, AggregationType aggregationType) 
	{
		this.id = id;
		this.property = property;
		this.multivalue = multivalue;
		this.aggregationType = aggregationType;
	}

	public PropertySettings()
	{	
	}
	
	public String getProperty() 
	{
		return property;
	}

	public boolean isMultivalue() 
	{
		return multivalue;
	}
	
	public AggregationType getAggregationType()
	{
		return aggregationType;
	}
	
	@Override
	public String toString()
	{
		return "[" + id + "; " + property + "; " + multivalue + "; " + aggregationType + "]";
	}
}
