package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class DNRuleDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNRuleDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<DNRule> dnRuleDao;
	private DaoForEntityWithSurrogateKey<DNRuleComponent> dnRuleComponentDao;

	public DNRuleDetailPage(final Long ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Detail", 
			"Show DN rule detail"
		);
		
		// prepare DAO objects
		//
		dnRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleDao.class);
		dnRuleComponentDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentDao.class);
		
		// register page components
		//
		addHelpWindow("dnRuleHelpWindow", "openDNRuleHelpWindow", new DNRuleHelpPanel("content"));
		addHelpWindow("dnRuleComponentHelpWindow", "openDNRuleComponentHelpWindow", new DNRuleComponentHelpPanel("content"));
		addRuleInformationSection(ruleId);
		addRuleComponentsSection(ruleId);
	}

	private void addRuleInformationSection(final Long ruleId)
	{
		DNRule rule = dnRuleDao.load(ruleId);
		
		setDefaultModel(createModelForOverview(dnRuleDao, ruleId));
		
		add(new Label("description"));
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class, 
				rule.getGroupId(),
				"showDNGroupDetailPage"
			)
		);
	}
	
	private void addRuleComponentsSection(Long ruleId) 
	{
		add(
			new RedirectWithParamButton(
				NewDNRuleComponentPage.class,
				ruleId, 
				"addNewComponentLink"
			)
		);
		
		addRuleComponentsTable(ruleId);
	}

	private void addRuleComponentsTable(Long ruleId) 
	{
		IDataProvider<DNRuleComponent> data = new DependentDataProvider<DNRuleComponent>(
			dnRuleComponentDao, 
			"ruleId", 
			ruleId
		);
		
		DataView<DNRuleComponent> dataView = new DataView<DNRuleComponent>("dnComponentsTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<DNRuleComponent> item) 
			{
				DNRuleComponent component = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<DNRuleComponent>(component));
				
				item.add(new Label("type", component.getType().getLabel()));
				item.add(new TruncatedLabel("modification", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteRawButton<DNRuleComponent>
					(
						dnRuleComponentDao,
						component.getId(),
						"component",
						new DeleteConfirmationMessage("rule component"),
						DNRuleDetailPage.this
					)
				);

				item.add(
					new RedirectWithParamButton
					(
						DNRuleComponentDetailPage.class, 
						component.getId(), 
						"showDNRuleComponentDetailPage"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditDNRuleComponentPage.class,
						component.getId(),
						"showEditDNRuleComponentPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}
