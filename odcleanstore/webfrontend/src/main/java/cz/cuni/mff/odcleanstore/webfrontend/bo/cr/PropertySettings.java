package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class PropertySettings extends BusinessObject 
{
	private static final long serialVersionUID = 1L;

	private String property;
	private MultivalueType multivalueType;
	private AggregationType aggregationType;
	
	public PropertySettings(Long id, String property, 
		MultivalueType multivalueType, AggregationType aggregationType) 
	{
		this.id = id;
		this.property = property;
		this.multivalueType = multivalueType;
		this.aggregationType = aggregationType;
	}

	public PropertySettings()
	{	
	}
	
	public String getProperty() 
	{
		return property;
	}

	public MultivalueType getMultivalueType()
	{
		return multivalueType;
	}
	
	public AggregationType getAggregationType()
	{
		return aggregationType;
	}
	
	@Override
	public String toString()
	{
		return "[" + id + "; " + property + "; " + multivalueType + "; " + aggregationType + "]";
	}
}
