package cz.cuni.mff.odcleanstore.webfrontend.pages;

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
	
	public LimitedEditingPage(String pageCrumbs, String pageTitle, Class<? extends DaoForAuthorableEntity<?>> authorableDao, Integer editedEntityId) {
		super(pageCrumbs, pageTitle);
		Integer authorId = this.daoLookupFactory.getDao(authorableDao).getAuthorId(editedEntityId);
		this.isEditable = AuthorizationHelper.isAuthorizedForEntityEditing(authorId);
	}
	
	/**
	 * Constructor with editing disabled.
	 *
	public LimitedEditingPage(String pageCrumbs, String pageTitle)
	{
		this(pageCrumbs, pageTitle, false);
	}*/

	protected boolean isEditable() 
	{
		return isEditable;
	}
}
