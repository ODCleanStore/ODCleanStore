package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class DNRuleDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNRuleDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<DNRule> dnRuleDao;

	public DNRuleDetailPage(final Long ruleId) 
	{
		super(
			"Home > QA > Rules groups > Group > Detail", 
			"QA Rules management"
		);
		
		// prepare DAO objects
		//
		dnRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleDao.class);
		
		// register page components
		//
		addRuleInformationSection(ruleId);
	}
	
	private void addRuleInformationSection(final Long ruleId)
	{
		DNRule rule = dnRuleDao.load(ruleId);
		
		setDefaultModel(createModelForOverview(dnRuleDao, ruleId));
		
		add(new Label("description"));
		
		add(
			new RedirectButton(
				DNGroupDetailPage.class, 
				rule.getGroupId(),
				"showDNGroupDetailPage"
			)
		);
	}
}
