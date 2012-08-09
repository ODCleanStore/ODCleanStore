package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.OIRuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.QARuleAssignmentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QAGroupsListPage;

public class OIGroupsListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(OIGroupsListPage.class);
	
	private DaoForEntityWithSurrogateKey<OIRulesGroup> oiRulesGroupsDao;
	private EngineOperationsDao engineOperationsDao;
	
	public OIGroupsListPage() 
	{
		super(
			"Home > OI > Rules groups > List", 
			"OI Rules management"
		);
		
		// prepare DAO objects
		//
		oiRulesGroupsDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRulesGroupDao.class);
		engineOperationsDao = daoLookupFactory.getEngineOperationsDao();
		
		// register page components
		//
		addOIRulesGroupsTable();
	}
	
	/*
	 	=======================================================================
	 	Implementace oiRulesGroupsTable
	 	=======================================================================
	*/
	
	private void addOIRulesGroupsTable()
	{
		IDataProvider<OIRulesGroup> data = new DataProvider<OIRulesGroup>(oiRulesGroupsDao);
		
		DataView<OIRulesGroup> dataView = new DataView<OIRulesGroup>("oiRulesGroupsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<OIRulesGroup> item) 
			{
				OIRulesGroup group = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<OIRulesGroup>(group));

				item.add(new Label("label"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteRawButton<OIRulesGroup>
					(
						oiRulesGroupsDao,
						group.getId(),
						"group",
						new DeleteConfirmationMessage("group", "rule"),
						OIGroupsListPage.this
					)
				);
				
				item.add(
					new RedirectButton(
						OIGroupDetailPage.class,
						group.getId(), 
						"manageRules"
					)
				);
				
				item.add(
					new RedirectButton(
						EditOIGroupPage.class,
						group.getId(),
						"showEditOIGroupPage"
					)
				);
				
				item.add(createRerunAffectedGraphsButton(group.getId()));
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
	private Link createRerunAffectedGraphsButton(final Long groupId)
	{
		Link button = new Link("rerunAffectedGraphs")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
				try {
					engineOperationsDao.rerunGraphsForRulesGroup(OIRuleAssignmentDao.TABLE_NAME, groupId);
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
				setResponsePage(OIGroupsListPage.class);
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
