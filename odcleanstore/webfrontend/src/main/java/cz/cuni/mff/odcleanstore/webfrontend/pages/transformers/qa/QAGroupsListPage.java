package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

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
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.QARuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ Role.PIC })
public class QAGroupsListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QAGroupsListPage.class);
	
	private QARulesGroupDao qaRulesGroupDao;
	private EngineOperationsDao engineOperationsDao;
	
	public QAGroupsListPage() 
	{
		super(
			"Home > Backend > QA > Groups > List", 
			"Show all QA rule groups"
		);
		
		// prepare DAO objects
		//
		qaRulesGroupDao = daoLookupFactory.getDao(QARulesGroupDao.class);
		engineOperationsDao = daoLookupFactory.getDao(EngineOperationsDao.class);
		
		// register page components
		//
		addHelpWindow(new RulesGroupHelpPanel("content"));
		addOIRulesGroupsTable();
	}
	
	private void addOIRulesGroupsTable()
	{
		SortableDataProvider<QARulesGroup> data = new GenericSortableDataProvider<QARulesGroup>(qaRulesGroupDao, "label");
		
		DataView<QARulesGroup> dataView = new DataView<QARulesGroup>("qaRulesGroupsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<QARulesGroup> item) 
			{
				QARulesGroup group = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<QARulesGroup>(group));

				item.add(new Label("label"));
				item.add(new Label("authorName"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));	
				
				item.add(
					new AuthorizedDeleteButton<QARulesGroup>
					(
						qaRulesGroupDao,
						group,
						"group",
						new DeleteConfirmationMessage("group", "rule"),
						QAGroupsListPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						QAGroupDetailPage.class,
						group.getId(),
						"showEditQAGroupPage"
					)
				);
				
				item.add(createRerunAffectedGraphsButton(group.getId()));
			}
		};

		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new SortTableButton<QARulesGroup>("orderByLabel", "label", data, dataView));
		add(new SortTableButton<QARulesGroup>("orderByAuthor", "username", data, dataView));
		
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
					engineOperationsDao.rerunGraphsForRulesGroup(QARuleAssignmentDao.TABLE_NAME, groupId);
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
				//setResponsePage(QAGroupsListPage.class);
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
