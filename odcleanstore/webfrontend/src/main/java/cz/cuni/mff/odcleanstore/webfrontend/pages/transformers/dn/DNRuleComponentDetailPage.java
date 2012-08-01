package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
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
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DetachableModel;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class DNRuleComponentDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNRuleComponentDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<DNRuleComponent> dnRuleComponentDao;

	public DNRuleComponentDetailPage(final Long ruleComponentId) 
	{
		super(
			"Home > QA > Rules groups > Group > Rule > Component > Detail", 
			"DN Rules management"
		);
		
		// prepare DAO objects
		//
		dnRuleComponentDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentDao.class);
		
		// register page components
		//
		addRuleComponentInformationSection(ruleComponentId);
	}

	private void addRuleComponentInformationSection(final Long ruleComponentId)
	{
		DNRuleComponent component = dnRuleComponentDao.load(ruleComponentId);
		
		setDefaultModel(createModelForOverview(dnRuleComponentDao, ruleComponentId));
		
		add(new Label("type", component.getType().getLabel()));
		add(new Label("modification"));
		add(new Label("description"));
		
		add(
			new RedirectButton(
				DNRuleDetailPage.class, 
				component.getRuleId(),
				"showDNRuleDetailPage"
			)
		);
	}
}