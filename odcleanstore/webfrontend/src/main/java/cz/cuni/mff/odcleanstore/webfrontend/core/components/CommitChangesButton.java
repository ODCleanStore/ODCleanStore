package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRulesGroupDao;

/**
 * Button for committing changes in a rule group.
 * @author Jan Michelfeit
 */
public class CommitChangesButton extends Link<String>
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CommitChangesButton.class);

	boolean isClickable;
	Integer groupId;
	AbstractRulesGroupDao<?> dao;

	public CommitChangesButton(String componentId, RulesGroupEntity group, AbstractRulesGroupDao<?> groupDao)
	{
		super(componentId);
		boolean isAuthorized = AuthorizationHelper.isAuthorizedForEntityEditing(group.getAuthorId());
		this.isClickable = group.isUncommitted() && isAuthorized;
		this.groupId = group.getId();
		this.dao = groupDao;
	}

	@Override
	public boolean isVisible()
	{
		return isClickable;
	}

	@Override
	public void onClick()
	{
		if (!isClickable)
		{
			return;
		}
		try
		{
			dao.commitChanges(groupId);
		}
		catch (Exception e)
		{
			logger.error(e);
			getSession().error("Commiting changes failed due to an unexpected error.");
			return;
		}
		getSession().info("Changes were successfully comitted.");
	}
}
