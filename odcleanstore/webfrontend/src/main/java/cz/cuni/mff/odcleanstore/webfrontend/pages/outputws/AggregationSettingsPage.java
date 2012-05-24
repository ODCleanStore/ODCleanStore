package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.GlobalAggregationSettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.PropertySettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class AggregationSettingsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(AggregationSettingsPage.class);
	
	private PropertySettingsDao propertySettingsDao;
	private GlobalAggregationSettingsDao globalAggregationSettingsDao;
	
	public AggregationSettingsPage() 
	{
		super(
			"Home > OutputWS > CR > Aggregation Settings", 
			"Adjust aggregation settings"
		);
		
		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getPropertySettingsDao();
		globalAggregationSettingsDao = daoLookupFactory.getGlobalAggregationSettingsDao();
		
		// register page components
		//
		addGlobalAggregationSettingsSection();
		addPropertySettingsTable();
	}

	private void addGlobalAggregationSettingsSection()
	{
		GlobalAggregationSettings settings = globalAggregationSettingsDao.load();
		
		String defaultAggregationType = settings.getDefaultAggregationType().getLabel();
		add(new Label("defaultAggregationType", defaultAggregationType));
		
		String defaultMultivalueType = settings.getDefaultMultivalueType().getLabel();
		add(new Label("defaultMultivalueType", defaultMultivalueType));
		
		String defaultErrorStrategy = settings.getDefaultErrorStrategy().getLabel();
		add(new Label("defaultErrorStrategy", defaultErrorStrategy));
	}
	
	private void addPropertySettingsTable()
	{
		List<PropertySettings> allProperties = propertySettingsDao.loadAll();
		
		ListView<PropertySettings> listView = new ListView<PropertySettings>("propertySettingsTable", allProperties)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<PropertySettings> item) 
			{
				final PropertySettings property = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<PropertySettings>(property));
				
				item.add(new Label("property"));
				
				String multivalueType = property.getMultivalueType().getLabel();
				item.add(new Label("multivalueType", multivalueType));
				
				String aggregationType = property.getAggregationType().getLabel();
				item.add(new Label("aggregationType", aggregationType));
				
				addDeleteButton(item, property);
			}
		};
		
		add(listView);
	}
	
	private void addDeleteButton(ListItem<PropertySettings> item, final PropertySettings property)
	{
		Link button = new Link("deleteProperty")
        {
            @Override
            public void onClick()
            {
            	logger.debug("About to delete property: " + property);
            	
            	propertySettingsDao.delete(property);
				
				getSession().info("The property was successfuly deleted.");
				setResponsePage(AggregationSettingsPage.class);
            }
        };
        
		item.add(button);
	}
}
