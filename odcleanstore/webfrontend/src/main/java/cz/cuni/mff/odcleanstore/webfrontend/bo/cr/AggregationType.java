package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent a type of CR aggregation policy.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class AggregationType extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 */
	public AggregationType(Integer id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() 
	{
		return label;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() 
	{
		return description;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AggregationType))
			return false;
		
		AggregationType other = (AggregationType) obj;
		
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}
}
