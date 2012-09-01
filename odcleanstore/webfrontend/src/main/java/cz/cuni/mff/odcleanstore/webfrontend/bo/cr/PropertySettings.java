package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;;

public class PropertySettings extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private String property;
	private MultivalueType multivalueType;
	private AggregationType aggregationType;
	
	public PropertySettings(Long id, String property, 
		MultivalueType multivalueType, AggregationType aggregationType) 
	{
		super(id);
		
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
		return "[ id: " + id + "; property: " + property + "; multivalue: " + multivalueType + "; aggregation: " + aggregationType + "]";
	}
}
