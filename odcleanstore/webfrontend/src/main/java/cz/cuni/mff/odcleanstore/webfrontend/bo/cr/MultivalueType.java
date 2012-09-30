package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent a type of CR multivalue policy.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class MultivalueType extends EntityWithSurrogateKey
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
	public MultivalueType(Integer id, String label, String description) 
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
	public String toString()
	{
		return "[ id: " + id + ", label: " + label + ", description: " + description + "]";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof MultivalueType))
			return false;
		
		MultivalueType other = (MultivalueType) obj;
		
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}
}

