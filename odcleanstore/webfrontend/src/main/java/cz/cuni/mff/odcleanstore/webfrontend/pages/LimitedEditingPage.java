package cz.cuni.mff.odcleanstore.webfrontend.pages;

import org.apache.wicket.authorization.UnauthorizedInstantiationException;

import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

/**
 * An abstract base class for pages that need an explicit flag passed in constructor to enable editing.
 * 
 * @author Jan Michelfeit
 */
public abstract class LimitedEditingPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private final boolean isEditable;

	public LimitedEditingPage(String pageCrumbs, String pageTitle,
		Class<? extends DaoForAuthorableEntity<?>> authorableDao, Integer editedEntityId)
	{
		super(pageCrumbs, pageTitle);
		Integer authorId = null;
		try
		{
			authorId = this.daoLookupFactory.getDao(authorableDao).getAuthorId(editedEntityId);
		}
		catch (RuntimeException originalException)
		{
			try
			{
				// The exception could be because the entity is still uncommitted
				// Let's try with uncommitted tables
				authorId = this.daoLookupFactory.getDao(authorableDao, true).getAuthorId(editedEntityId);
			}
			catch (Exception e)
			{
				// OK, the error was in something else, throw original exception
				throw originalException;
			}
		}
		this.isEditable = AuthorizationHelper.isAuthorizedForEntityEditing(authorId);
	}

	protected boolean isEditable()
	{
		return isEditable;
	}
	
	protected void checkUnathorizedInstantiation() throws UnauthorizedInstantiationException
	{
		if (!isEditable) 
		{
			throw new UnauthorizedInstantiationException(getClass());
		}
	}
}
