package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * A button to delete DN rule template instances.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <BO>
 */
public class AuthorizedDeleteTemplateInstanceButton<BO extends DNTemplateInstance> 
	extends AuthorizedDeleteButton<BO>
{
	private static final long serialVersionUID = 1L;
	
	private DNTemplateInstanceDao<BO> templateInstanceDao;
	private BO bo;
	
	/**
	 * 
	 * @param templateInstanceDao
	 * @param compiledRuleDao
	 * @param bo
	 * @param isAuthorized
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public AuthorizedDeleteTemplateInstanceButton(
		DNTemplateInstanceDao<BO> templateInstanceDao,
		BO bo, boolean isAuthorized, String objName, 
		DeleteConfirmationMessage message, FrontendPage redirectPage)
	{
		super(templateInstanceDao, bo.getId(), isAuthorized, objName, message, redirectPage);
		
		this.templateInstanceDao = templateInstanceDao;
		this.bo = bo;
	}

	@Override
	protected void delete() throws Exception
	{
		if (isAuthorized)
		{
			templateInstanceDao.delete(bo);
		}
	}
}
