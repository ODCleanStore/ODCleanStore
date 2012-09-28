package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ Role.PIC })
public class DNGroupDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNGroupDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<DNRulesGroup> dnRulesGroupDao;
	private DaoForEntityWithSurrogateKey<DNRule> dnRuleDao;
	private DaoForEntityWithSurrogateKey<DNReplaceTemplateInstance> dnReplaceTemplateInstanceDao;
	private DaoForEntityWithSurrogateKey<DNRenameTemplateInstance> dnRenameTemplateInstanceDao;

	public DNGroupDetailPage(final Integer groupId) 
	{
		super(
			"Home > Backend > DN > Groups > Detail", 
			"Show DN rule group detail"
		);
		
		// prepare DAO objects
		//
		dnRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRulesGroupDao.class);
		dnRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleDao.class);
		dnReplaceTemplateInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNReplaceTemplateInstanceDao.class);
		dnRenameTemplateInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRenameTemplateInstanceDao.class);
		
		// register page components
		//
		addHelpWindow("rulesGroupHelpWindow", "openRulesGroupHelpWindow", new RulesGroupHelpPanel("content"));
		addHelpWindow("dnRuleHelpWindow", "openDNRuleHelpWindow", new DNRuleHelpPanel("content"));
		addHelpWindow("dnReplaceTemplateInstanceHelpWindow", "openDNReplaceTemplateInstanceHelpWindow", new DNReplaceTemplateInstanceHelpPanel("content"));
		addHelpWindow("dnRenameTemplateInstanceHelpWindow", "openDNRenameTemplateInstanceHelpWindow", new DNRenameTemplateInstanceHelpPanel("content"));
		
		addGroupInformationSection(groupId);
		
		addDNRawRulesSection(groupId);
		addDNReplaceTemplateInstancesSection(groupId);
		addDNRenameTemplateInstancesSection(groupId);
		addDNFilterTemplateInstancesSection(groupId);
	}

	private void addGroupInformationSection(final Integer groupId)
	{
		setDefaultModel(createModelForOverview(dnRulesGroupDao, groupId));
		
		add(new Label("label"));
		add(new Label("description"));
	}
	
	private void addDNRawRulesSection(final Integer groupId) 
	{
		add(
			new RedirectWithParamButton(
				NewDNRulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addDNRawRulesTable(groupId);
	}

	private void addDNRawRulesTable(final Integer groupId)
	{
		IDataProvider<DNRule> data = new DependentDataProvider<DNRule>(dnRuleDao, "groupId", groupId);
		
		DataView<DNRule> dataView = new DataView<DNRule>("dnRawRulesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<DNRule> item) 
			{
				DNRule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<DNRule>(rule));
				
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteRawButton<DNRule>
					(
						dnRuleDao,
						rule.getId(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						DNGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						DNRuleDetailPage.class, 
						rule.getId(), 
						"showDNRuleDetailPage"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditDNRulePage.class,
						rule.getId(),
						"showEditDNRulePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("rawRulesNavigator", dataView));
	}
	
	private void addDNReplaceTemplateInstancesSection(final Integer groupId) 
	{
		add(
			new RedirectWithParamButton(
				NewDNReplaceTemplateInstancePage.class,
				groupId, 
				"addNewReplaceTemplateInstanceLink"
			)
		);
		
		addDNReplaceTemplateInstancesTable(groupId);
	}
	
	private void addDNReplaceTemplateInstancesTable(final Integer groupId)
	{
		IDataProvider<DNReplaceTemplateInstance> data = new DependentDataProvider<DNReplaceTemplateInstance>
		(
			dnReplaceTemplateInstanceDao, 
			"groupId", 
			groupId
		);
	
		DataView<DNReplaceTemplateInstance> dataView = new DataView<DNReplaceTemplateInstance>("dnReplaceTemplateInstancesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<DNReplaceTemplateInstance> item) 
			{
				DNReplaceTemplateInstance instance = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<DNReplaceTemplateInstance>(instance));
	
				item.add(new TruncatedLabel("propertyName", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("pattern", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("replacement", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteRawButton<DNReplaceTemplateInstance>
					(
						dnReplaceTemplateInstanceDao,
						instance.getId(),
						"replaceTemplateInstance",
						new DeleteConfirmationMessage("replace template instance"),
						DNGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						DNReplaceTemplateInstanceDetailPage.class,
						instance.getId(), 
						"showDNReplaceTemplateInstanceDetailPage"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditDNReplaceTemplateInstancePage.class,
						instance.getId(),
						"showEditDNReplaceTemplateInstancePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("replaceTemplateInstancesNavigator", dataView));
	}
	
	private void addDNRenameTemplateInstancesSection(final Integer groupId) 
	{
		add(
			new RedirectWithParamButton(
				NewDNRenameTemplateInstancePage.class,
				groupId, 
				"addNewRenameTemplateInstanceLink"
			)
		);
		
		addDNRenameTemplateInstancesTable(groupId);
	}
	
	private void addDNRenameTemplateInstancesTable(final Integer groupId)
	{
		IDataProvider<DNRenameTemplateInstance> data = new DependentDataProvider<DNRenameTemplateInstance>
		(
			dnRenameTemplateInstanceDao, 
			"groupId", 
			groupId
		);
	
		DataView<DNRenameTemplateInstance> dataView = new DataView<DNRenameTemplateInstance>("dnRenameTemplateInstancesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<DNRenameTemplateInstance> item) 
			{
				DNRenameTemplateInstance instance = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<DNRenameTemplateInstance>(instance));
	
				item.add(new TruncatedLabel("sourcePropertyName", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("targetPropertyName", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteRawButton<DNRenameTemplateInstance>
					(
						dnRenameTemplateInstanceDao,
						instance.getId(),
						"renameTemplateInstance",
						new DeleteConfirmationMessage("rename template instance"),
						DNGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						DNRenameTemplateInstanceDetailPage.class,
						instance.getId(), 
						"showDNRenameTemplateInstanceDetailPage"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditDNRenameTemplateInstancePage.class,
						instance.getId(),
						"showEditDNRenameTemplateInstancePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("renameTemplateInstancesNavigator", dataView));
	}
	
	private void addDNFilterTemplateInstancesSection(final Integer groupId) 
	{
		add(
			new RedirectWithParamButton(
				NewDNFilterTemplateInstancePage.class,
				groupId, 
				"addNewFilterTemplateInstanceLink"
			)
		);
		
		// addDNRenameTemplateInstancesTable(groupId);
	}
}
