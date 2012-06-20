package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class QAGroupsManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<QARulesGroup> qaRulesGroupDao;
	
	public QAGroupsManagementPage() 
	{
		super(
			"Home > Transformers > OI > Rules management", 
			"OI Rules management"
		);
		
		// prepare DAO objects
		//
		qaRulesGroupDao = daoLookupFactory.getDao(QARulesGroupDao.class);
		
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
		IDataProvider<QARulesGroup> data = new DataProvider<QARulesGroup>(qaRulesGroupDao);
		
		DataView<QARulesGroup> dataView = new DataView<QARulesGroup>("qaRulesGroupsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<QARulesGroup> item) 
			{
				QARulesGroup group = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<QARulesGroup>(group));

				item.add(new Label("label"));
				item.add(new Label("description"));	
				
				item.add(
					new DeleteButton<QARulesGroup>
					(
						qaRulesGroupDao,
						group.getId(),
						"group",
						new DeleteConfirmationMessage("group", "rule"),
						QAGroupsManagementPage.this
					)
				);
				
				item.add(
					createGoToPageButton(
						QARulesManagementPage.class,
						group.getId(), 
						"manageRules"
					)
				);	
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
