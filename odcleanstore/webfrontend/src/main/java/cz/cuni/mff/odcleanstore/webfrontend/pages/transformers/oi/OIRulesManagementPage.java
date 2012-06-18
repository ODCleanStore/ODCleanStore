package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class OIRulesManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<OIRulesGroup> oiRulesGroupsDao;
	
	public OIRulesManagementPage() 
	{
		super(
			"Home > Transformers > OI > Rules management", 
			"OI Rules management"
		);
		
		// prepare DAO objects
		//
		oiRulesGroupsDao = daoLookupFactory.getDao(OIRulesGroupDao.class);
		
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
				item.add(new Label("description"));	
				
				item.add(
					createDeleteButton(
						oiRulesGroupsDao,
						group,
						"deleteGroup",
						"group",
						"rule",
						OIRulesManagementPage.class
					)
				);
				
				item.add(
					createGoToPageButton(
						ManageGroupRulesPage.class,
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
