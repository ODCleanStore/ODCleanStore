package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RuleEntity;

public class DNRule extends RuleEntity
{
	private static final long serialVersionUID = 1L;

	private String description;
	
	public DNRule(Integer id, Integer groupId, String description) 
	{
		super(id, groupId);
		
		this.description = description;
	}
	
	public DNRule()
	{
	}

	public String getDescription() 
	{
		return description;
	}
}
