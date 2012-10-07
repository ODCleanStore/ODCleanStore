package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

/**
 * The BO to represent a group of DN rules.
 * 
 * @author Dusan
 *
 */
public class DNRulesGroup extends RulesGroupEntity implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;

	public DNRulesGroup(Integer id, String label, String description, Integer authorId, boolean isUncommitted, String authorName) 
	{
		super(id, label, description, authorId, isUncommitted, authorName);
	}

	/**
	 * 
	 */
	public DNRulesGroup() 
	{
	}
}
