package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class MultivalueType extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	public MultivalueType(Long id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
	}

	public String getLabel() 
	{
		return label;
	}

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

