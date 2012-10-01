package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

public class QARulesGroup extends RulesGroupEntity implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;

	public QARulesGroup(Integer id, String label, String description, Integer authorId, boolean isUncommitted, String authorName) 
	{
		super(id, label, description, authorId, isUncommitted, authorName);
	}

	public QARulesGroup()
	{
		
	}
}
