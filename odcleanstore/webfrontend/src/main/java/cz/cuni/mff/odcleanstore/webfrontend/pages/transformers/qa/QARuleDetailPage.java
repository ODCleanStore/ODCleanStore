package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class QARuleDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QARuleDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<QARule> qaRuleDao;

	public QARuleDetailPage(final Long ruleId) 
	{
		super(
			"Home > Backend > QA > Groups > Rules > Detail", 
			"Show a QA rule detail"
		);
		
		// prepare DAO objects
		//
		qaRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(QARuleDao.class);
		
		// register page components
		//
		addHelpWindow(new QARuleHelpPanel("content"));
		addRuleInformationSection(ruleId);
	}
	
	private void addRuleInformationSection(final Long ruleId)
	{
		QARule rule = qaRuleDao.load(ruleId);
		
		setDefaultModel(createModelForOverview(qaRuleDao, ruleId));
		
		add(new Label("filter"));
		add(new Label("coefficient"));
		add(new Label("description"));
		
		add(
			new RedirectWithParamButton(
				QAGroupDetailPage.class, 
				rule.getGroupId(),
				"showQAGroupDetailPage"
			)
		);
	}
}
