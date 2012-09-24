package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class DNRuleComponentDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNRuleComponentDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<DNRuleComponent> dnRuleComponentDao;

	public DNRuleComponentDetailPage(final Integer ruleComponentId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Components > Detail", 
			"Show DN rule component detail"
		);
		
		// prepare DAO objects
		//
		addHelpWindow(new DNRuleComponentHelpPanel("content"));
		dnRuleComponentDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentDao.class);
		
		// register page components
		//
		addRuleComponentInformationSection(ruleComponentId);
	}

	private void addRuleComponentInformationSection(final Integer ruleComponentId)
	{
		DNRuleComponent component = dnRuleComponentDao.load(ruleComponentId);
		
		setDefaultModel(createModelForOverview(dnRuleComponentDao, ruleComponentId));
		
		add(new Label("type", component.getType().getLabel()));
		add(new Label("modification"));
		add(new Label("description"));
		
		add(
			new RedirectWithParamButton(
				DNRuleDetailPage.class, 
				component.getRuleId(),
				"showDNRuleDetailPage"
			)
		);
	}
}
