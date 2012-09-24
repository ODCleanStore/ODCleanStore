package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ "PIC" })
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
