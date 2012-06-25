package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class AggregationType extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	public AggregationType(Long id, String label, String description) 
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
}
