package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.DNRuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.OIRuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.QARuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.EditDNGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.NewDNGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.EditOIGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.NewOIGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.EditQAGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.NewQAGroupPage;

@AuthorizeInstantiation({ Role.PIC })
public class AssignedGroupsListPageFactory 
{
	public static AssignedGroupsList createAssignedQAGroupsList(
		DaoLookupFactory daoLookupFactory, Integer transformerInstanceId
	)
	{
		return new AssignedGroupsList(
			"assignedGroupsListSection", 
			transformerInstanceId, 
			daoLookupFactory.getDaoForEntityWithSurrogateKey(QARulesGroupDao.class), 
			daoLookupFactory.getDaoForEntityWithSurrogateKey(QARuleAssignmentDao.class), 
			EditQAGroupPage.class,
			NewQAGroupPage.class
		);
	}
	
	public static AssignedGroupsList createAssignedOIGroupsList(
		DaoLookupFactory daoLookupFactory, Integer transformerInstanceId
	)
	{
		return new AssignedGroupsList(
			"assignedGroupsListSection", 
			transformerInstanceId, 
			daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRulesGroupDao.class), 
			daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRuleAssignmentDao.class), 
			EditOIGroupPage.class,
			NewOIGroupPage.class
		);
	}
	
	public static AssignedGroupsList createAssignedDNGroupsList(
		DaoLookupFactory daoLookupFactory, Integer transformerInstanceId
	)
	{
		return new AssignedGroupsList(
			"assignedGroupsListSection", 
			transformerInstanceId, 
			daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRulesGroupDao.class), 
			daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleAssignmentDao.class), 
			EditDNGroupPage.class,
			NewDNGroupPage.class
		);
	}
}
