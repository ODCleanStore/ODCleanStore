package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;;

/**
 * The BO to represent the CR aggregation settings bound to a specific
 * RDF property.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class PropertySettings extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private String property;
	private MultivalueType multivalueType;
	private AggregationType aggregationType;
	
	/**
	 * 
	 * @param id
	 * @param property
	 * @param multivalueType
	 * @param aggregationType
	 */
	public PropertySettings(Integer id, String property, 
		MultivalueType multivalueType, AggregationType aggregationType) 
	{
		super(id);
		
		this.property = property;
		this.multivalueType = multivalueType;
		this.aggregationType = aggregationType;
	}

	/**
	 * 
	 */
	public PropertySettings()
	{	
	}
	
	/**
	 * 
	 * @return
	 */
	public String getProperty() 
	{
		return property;
	}

	/**
	 * 
	 * @return
	 */
	public MultivalueType getMultivalueType()
	{
		return multivalueType;
	}
	
	/**
	 * 
	 * @return
	 */
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
