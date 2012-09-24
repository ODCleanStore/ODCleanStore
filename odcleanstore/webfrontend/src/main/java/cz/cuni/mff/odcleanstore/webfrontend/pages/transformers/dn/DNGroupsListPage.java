package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.DNRuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.OIRuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIGroupsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QAGroupsListPage;

@AuthorizeInstantiation({ "PIC" })
public class DNGroupsListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(OIGroupsListPage.class);
	
	private DaoForEntityWithSurrogateKey<DNRulesGroup> dnRulesGroupDao;
	private EngineOperationsDao engineOperationsDao;
	
	public DNGroupsListPage() 
	{
		super(
			"Home > Backend > DN > Groups > List", 
			"List all DN rule groups"
		);
		
		// prepare DAO objects
		//
		dnRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRulesGroupDao.class);
		engineOperationsDao = daoLookupFactory.getEngineOperationsDao();
		
		// register page components
		//
		addHelpWindow(new RulesGroupHelpPanel("content"));
		addDNRulesGroupsTable();
	}
	
	private void addDNRulesGroupsTable()
	{
		SortableDataProvider<DNRulesGroup> data = new GenericSortableDataProvider<DNRulesGroup>(dnRulesGroupDao, "label");
		
		DataView<DNRulesGroup> dataView = new DataView<DNRulesGroup>("dnRulesGroupsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<DNRulesGroup> item) 
			{
				DNRulesGroup group = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<DNRulesGroup>(group));

				item.add(new Label("label"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));	
				
				item.add(
					new DeleteRawButton<DNRulesGroup>
					(
						dnRulesGroupDao,
						group.getId(),
						"group",
						new DeleteConfirmationMessage("group", "rule"),
						DNGroupsListPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						DNGroupDetailPage.class,
						group.getId(), 
						"manageRules"
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						EditDNGroupPage.class,
						group.getId(),
						"showEditDNGroupPage"
					)
				);
				
				item.add(createRerunAffectedGraphsButton(group.getId()));
			}
		};

		dataView.setItemsPerPage(ITEMS_PER_PAGE);

		add(new SortTableButton<DNRulesGroup>("orderByLabel", "label", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
	private Link createRerunAffectedGraphsButton(final Integer groupId)
	{
		Link button = new Link("rerunAffectedGraphs")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				try {
					engineOperationsDao.rerunGraphsForRulesGroup(DNRuleAssignmentDao.TABLE_NAME, groupId);
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());

					getSession().error(
						"The affected graphs could not be marked to be rerun due to an unexpected error."
					);

					return;
				}

				getSession().info("The affected graphs were successfuly marked to be rerun.");
				setResponsePage(DNGroupsListPage.class);
			}
		};

		button.add(
			new ConfirmationBoxRenderer(
				"Are you sure you want to rerun all affected graphs?"
			)
		);

		return button;
	}
}
