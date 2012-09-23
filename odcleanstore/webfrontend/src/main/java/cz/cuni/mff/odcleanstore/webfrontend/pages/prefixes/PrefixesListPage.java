package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.Prefix;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes.PrefixDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.ADM })
public class PrefixesListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private Dao<Prefix> prefixMappingDao;
	
	public PrefixesListPage() 
	{
		super
		(
			"Home > Namespace Prefixes > List", 
			"List all namespace prefixes"
		);
		
		// prepare DAO objects
		//
		prefixMappingDao = daoLookupFactory.getDao(PrefixDao.class);
		
		// register page components
		//
		addHelpWindow(new URLPrefixHelpPanel("content"));
		addPrefixesTable();
	}
	
	private void addPrefixesTable()
	{
		SortableDataProvider<Prefix> data = new SortablePrefixDataProvider(prefixMappingDao, "NS_PREFIX");
		
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
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);

		add(new SortTableButton<Prefix>("sortByPrefix", "NS_PREFIX", data, dataView));
		add(new SortTableButton<Prefix>("sortByURL", "NS_URL", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
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
				
				setResponsePage(PrefixesListPage.this);
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
