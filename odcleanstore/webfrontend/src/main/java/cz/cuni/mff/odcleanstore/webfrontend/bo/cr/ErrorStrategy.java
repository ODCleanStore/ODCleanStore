package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class ErrorStrategy extends EntityWithSurrogateKey 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	public ErrorStrategy(Integer id, String label, String description) 
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
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ErrorStrategy))
			return false;
		
		ErrorStrategy other = (ErrorStrategy) obj;
		
		return this.id.equals(other.id);
	}
	
	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}
}
