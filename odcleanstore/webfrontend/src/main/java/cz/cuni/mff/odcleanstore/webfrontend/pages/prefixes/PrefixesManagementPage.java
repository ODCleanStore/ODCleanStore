package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes.PrefixDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class PrefixesManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private Dao<Prefix> prefixMappingDao;
	
	public PrefixesManagementPage() 
	{
		super
		(
			"Home > Prefixes", 
			"Prefixes configuration"
		);
		
		// prepare DAO objects
		//
		prefixMappingDao = daoLookupFactory.getDao(PrefixDao.class);
		
		// register page components
		//
		addPrefixesTable();
	}
	
	/*
	 	=======================================================================
	 	Implementace transformersTable
	 	=======================================================================
	*/
	
	private void addPrefixesTable()
	{
		IDataProvider<Prefix> data = new PrefixDataProvider(prefixMappingDao);
		
		DataView<Prefix> dataView = new DataView<Prefix>("prefixesTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<Prefix> item) 
			{
				Prefix mapping = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Prefix>(mapping));
				
				item.add(new Label("prefix"));
				item.add(new Label("url"));
				
				item.add(createDeletePrefixButton(mapping));
			}
		};
		
		dataView.setItemsPerPage(10);

		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
	private Link createDeletePrefixButton(final Prefix mapping)
	{
		Link button = new Link("deletePrefix")
	    {
			private static final long serialVersionUID = 1L;
	
			@Override
	        public void onClick()
	        {
	        	try {
					prefixMappingDao.delete(mapping);
				} 
	        	catch (Exception e) 
	        	{
	        		getSession().error("Could not delete prefix mapping due to an unexpected error.");
	        		return;
				}
	        	
				getSession().info("The prefix mapping was successfuly deleted.");
				
				setResponsePage(PrefixesManagementPage.this);
	        }
	    };
	    
	    button.add(
	    	new ConfirmationBoxRenderer(
	    		"Are you sure you want to delete the prefix mapping?"
	    	)
	    );
	    
	    return button;
	}
}
