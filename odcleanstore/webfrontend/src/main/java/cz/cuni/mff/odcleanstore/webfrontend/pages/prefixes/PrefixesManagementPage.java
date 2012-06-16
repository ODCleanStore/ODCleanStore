package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.PrefixMapping;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes.PrefixMappingDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class PrefixesManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private Dao<PrefixMapping> prefixMappingDao;
	
	public PrefixesManagementPage() 
	{
		super
		(
			"Home > Prefixes", 
			"Prefixes configuration"
		);
		
		// prepare DAO objects
		//
		prefixMappingDao = daoLookupFactory.getDao(PrefixMappingDao.class);
		
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
		IModel<List<PrefixMapping>> model = createModelForListView(prefixMappingDao);
		
		ListView<PrefixMapping> listView = new ListView<PrefixMapping>("prefixesTable", model)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<PrefixMapping> item) 
			{
				final PrefixMapping mapping = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<PrefixMapping>(mapping));
	
				item.add(new Label("prefix"));
				item.add(new Label("url"));
				
				item.add(createDeletePrefixButton(mapping));
			}
		};
		
		add(listView);
	}
	
	private Link createDeletePrefixButton(final PrefixMapping mapping)
	{
		Link button = new Link("deletePrefix")
	    {
			private static final long serialVersionUID = 1L;
	
			@Override
	        public void onClick()
	        {
	        	prefixMappingDao.delete(mapping);
	        	
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
