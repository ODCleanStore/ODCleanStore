package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

/**
 * The BO to represent a group of DN rules.
 * 
 * @author Dušan Rychnovský
 *
 */
public class DNRulesGroup extends RulesGroupEntity
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param authorId
	 * @param isUncommitted
	 * @param authorName
	 */
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
