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
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.DNGroupDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.NewDNGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIGroupDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.NewOIGroupPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QAGroupDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.NewQAGroupPage;

/**
 * A factory to instantiate list-of-assigned-groups panels according
 * to transformer types.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class AssignedGroupsListPageFactory 
{
	/**
	 * 
	 * @param daoLookupFactory
	 * @param transformerInstanceId
	 * @return
	 */
	public static AssignedGroupsList createAssignedQAGroupsList(
		DaoLookupFactory daoLookupFactory, Integer transformerInstanceId
	)
	{
		return new AssignedGroupsList(
			"assignedGroupsListSection", 
			transformerInstanceId, 
			daoLookupFactory,
			daoLookupFactory.getDao(QARulesGroupDao.class), 
			daoLookupFactory.getDao(QARuleAssignmentDao.class), 
			QAGroupDetailPage.class,
			NewQAGroupPage.class
		);
	}
	
	/**
	 * 
	 * @param daoLookupFactory
	 * @param transformerInstanceId
	 * @return
	 */
	public static AssignedGroupsList createAssignedOIGroupsList(
		DaoLookupFactory daoLookupFactory, Integer transformerInstanceId
	)
	{
		return new AssignedGroupsList(
			"assignedGroupsListSection", 
			transformerInstanceId, 
			daoLookupFactory,
			daoLookupFactory.getDao(OIRulesGroupDao.class), 
			daoLookupFactory.getDao(OIRuleAssignmentDao.class), 
			OIGroupDetailPage.class,
			NewOIGroupPage.class
		);
	}
	
	/**
	 * 
	 * @param daoLookupFactory
	 * @param transformerInstanceId
	 * @return
	 */
	public static AssignedGroupsList createAssignedDNGroupsList(
		DaoLookupFactory daoLookupFactory, Integer transformerInstanceId
	)
	{
		return new AssignedGroupsList(
			"assignedGroupsListSection", 
			transformerInstanceId, 
			daoLookupFactory,
			daoLookupFactory.getDao(DNRulesGroupDao.class), 
			daoLookupFactory.getDao(DNRuleAssignmentDao.class), 
			DNGroupDetailPage.class,
			NewDNGroupPage.class
		);
	}
}
