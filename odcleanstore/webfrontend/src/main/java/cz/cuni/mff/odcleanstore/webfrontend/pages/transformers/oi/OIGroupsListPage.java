package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class OIGroupsListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<OIRulesGroup> oiRulesGroupsDao;
	
	public OIGroupsListPage() 
	{
		super(
			"Home > OI > Rules groups > List", 
			"OI Rules management"
		);
		
		// prepare DAO objects
		//
		oiRulesGroupsDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRulesGroupDao.class);
		
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
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
