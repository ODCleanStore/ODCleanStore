package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.CommitChangesButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.DNRuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.debug.DNDebugPage;

@AuthorizeInstantiation({ Role.PIC })
public class DNGroupsListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(DNGroupsListPage.class);
	
	private DNRulesGroupDao dnRulesGroupDao;
	private EngineOperationsDao engineOperationsDao;
	
	public DNGroupsListPage() 
	{
		super(
			"Home > Backend > Data Normalization > Groups > List", 
			"List all Data Normalization rule groups"
		);
		
		// prepare DAO objects
		//
		dnRulesGroupDao = daoLookupFactory.getDao(DNRulesGroupDao.class);
		engineOperationsDao = daoLookupFactory.getDao(EngineOperationsDao.class);
		
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
				item.add(new Label("authorName"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));	
				
				item.add(
					new AuthorizedDeleteButton<DNRulesGroup>
					(
						dnRulesGroupDao,
						group,
						"group",
						new DeleteConfirmationMessage("group", "rule"),
						DNGroupsListPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						DNGroupDetailPage.class,
						group.getId(),
						"showEditDNGroupPage"
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						DNDebugPage.class,
						group.getId(),
						"debugDNGroup"
					)
				);
				
				item.add(createRerunAffectedGraphsButton(group.getId()));
				item.add(new CommitChangesButton("commitChanges", group, dnRulesGroupDao));
			}
		};

		dataView.setItemsPerPage(ITEMS_PER_PAGE);

		add(new SortTableButton<DNRulesGroup>("orderByLabel", "label", data, dataView));
		add(new SortTableButton<DNRulesGroup>("orderByAuthor", "username", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
	private Link<String> createRerunAffectedGraphsButton(final Integer groupId)
	{
		Link<String> button = new Link<String>("rerunAffectedGraphs")
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
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The affected graphs could not be marked to be rerun due to an unexpected error."
					);

					return;
				}

				getSession().info("The affected graphs were successfuly marked to be rerun.");
				//setResponsePage(DNGroupsListPage.class);
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
