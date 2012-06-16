package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
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
		IModel<List<OIRulesGroup>> model = createModelForListView(oiRulesGroupsDao);
		
		ListView<OIRulesGroup> listView = new ListView<OIRulesGroup>("oiRulesGroupsTable", model)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<OIRulesGroup> item) 
			{
				final OIRulesGroup group = item.getModelObject();
				
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
		
		add(listView);
	}
}
