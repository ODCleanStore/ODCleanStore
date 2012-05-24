package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class ErrorStrategy extends BusinessObject 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	public ErrorStrategy(Long id, String label, String description) 
	{
		this.id = id;
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
