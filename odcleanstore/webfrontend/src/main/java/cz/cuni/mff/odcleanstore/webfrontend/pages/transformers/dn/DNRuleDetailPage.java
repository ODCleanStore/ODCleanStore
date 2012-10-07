package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedRedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ Role.PIC })
public class DNRuleDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DNRuleDetailPage.class);
	
	private DNRuleDao dnRuleDao;
	private DNRuleComponentDao dnRuleComponentDao;

	public DNRuleDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Edit", 
			"Edit DN rule",
			DNRuleDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		dnRuleDao = daoLookupFactory.getDao(DNRuleDao.class, isEditable());
		dnRuleComponentDao = daoLookupFactory.getDao(DNRuleComponentDao.class, isEditable());
		
		// register page components
		//
		DNRule rule = dnRuleDao.load(ruleId);
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class, 
				rule.getGroupId(),
				"showDNGroupDetailPage"
			)
		);
		addHelpWindow("dnRuleHelpWindow", "openDNRuleHelpWindow", new DNRuleHelpPanel("content"));
		addHelpWindow("dnRuleComponentHelpWindow", "openDNRuleComponentHelpWindow", new DNRuleComponentHelpPanel("content"));
		addEditDNRuleForm(rule);
		addRuleComponentsSection(ruleId);
	}

	private void addEditDNRuleForm(final DNRule rule)
	{
		IModel<DNRule> formModel = new CompoundPropertyModel<DNRule>(rule);
		
		Form<DNRule> form = new LimitedEditingForm<DNRule>("editDNRuleForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				DNRule rule = this.getModelObject();
				
				try {
					dnRuleDao.update(rule);
				}
				catch (DaoException ex)
				{	
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The rule could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly updated.");
			}
		};
		
		form.add(createTextarea("description", false));
		
		add(form);
	}
	
	private void addRuleComponentsSection(Integer ruleId) 
	{
		add(
			new AuthorizedRedirectButton(
				NewDNRuleComponentPage.class,
				ruleId, 
				isEditable(),
				"addNewComponentLink"
			)
		);
		
		addRuleComponentsTable(ruleId);
	}

	private void addRuleComponentsTable(Integer ruleId) 
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
					new AuthorizedDeleteButton<DNRuleComponent>
					(
						dnRuleComponentDao,
						component.getId(),
						isEditable(),
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
