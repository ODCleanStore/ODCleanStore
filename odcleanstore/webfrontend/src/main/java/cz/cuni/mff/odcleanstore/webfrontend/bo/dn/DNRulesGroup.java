package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

public class DNRulesGroup extends RulesGroupEntity implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;

	public DNRulesGroup(Integer id, String label, String description, Integer authorId, String authorName) 
	{
		super(id, label, description, authorId, authorName);
	}

	public DNRulesGroup()
	{
	}
}
