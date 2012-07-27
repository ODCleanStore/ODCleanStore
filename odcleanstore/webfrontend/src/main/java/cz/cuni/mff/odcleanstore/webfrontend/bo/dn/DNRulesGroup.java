package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

public class DNRulesGroup extends RulesGroupEntity 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	public DNRulesGroup(Long id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
	}

	public DNRulesGroup() 
	{
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
