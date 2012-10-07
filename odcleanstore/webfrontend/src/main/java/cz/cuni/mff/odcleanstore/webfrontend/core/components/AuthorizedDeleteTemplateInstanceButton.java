package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.CompiledDNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class AuthorizedDeleteTemplateInstanceButton<BO extends DNTemplateInstance> 
	extends AuthorizedDeleteButton<BO>
{
	private static final long serialVersionUID = 1L;
	
	private DNTemplateInstanceDao<BO> templateInstanceDao;
	private CompiledDNRuleDao compiledRuleDao;
	private BO bo;
	
	public AuthorizedDeleteTemplateInstanceButton(
		DNTemplateInstanceDao<BO> templateInstanceDao,
		CompiledDNRuleDao compiledRuleDao,
		BO bo, boolean isAuthorized, String objName, 
		DeleteConfirmationMessage message, FrontendPage redirectPage)
	{
		super(templateInstanceDao, bo.getId(), isAuthorized, objName, message, redirectPage);
		
		this.templateInstanceDao = templateInstanceDao;
		this.compiledRuleDao = compiledRuleDao;
		this.bo = bo;
	}

	@Override
	protected void delete() throws Exception
	{
		if (isAuthorized)
		{
			// TODO: to be put into a transaction
			templateInstanceDao.delete(bo);
			compiledRuleDao.delete(bo.getRawRuleId());
		}
	}
}
