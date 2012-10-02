package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

/**
 * The BO which represents a group of QA rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class QARulesGroup extends RulesGroupEntity implements AuthoredEntity
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
	public QARulesGroup(Integer id, String label, String description, Integer authorId, boolean isUncommitted, String authorName) 
	{
		super(id, label, description, authorId, isUncommitted, authorName);
	}

	/**
	 * 
	 */
	public QARulesGroup()
	{
	}
}
