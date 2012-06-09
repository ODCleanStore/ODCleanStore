package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import java.util.List;

import org.apache.log4j.Logger;

public class AggregationSettingsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(AggregationSettingsPage.class);
	
	private Dao<PropertySettings> propertySettingsDao;
	private GlobalAggregationSettingsDao globalAggregationSettingsDao;
	
	public AggregationSettingsPage() 
	{
		super(
			"Home > OutputWS > CR > Aggregation Settings", 
			"Adjust aggregation settings"
		);
		
		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getDao(PropertySettingsDao.class);
		globalAggregationSettingsDao = daoLookupFactory.getGlobalAggregationSettingsDao();
		
		// register page components
		//
		addGlobalAggregationSettingsSection();
		addPropertySettingsTable();
	}

	private void addGlobalAggregationSettingsSection()
	{
		GlobalAggregationSettings settings = globalAggregationSettingsDao.load();
		
		addDefaultAggregationTypeLabel(settings);
		addDefaultMultivalueTypeLabel(settings);
		addDefaultErrorStrategy(settings);
	}
	
	private void addDefaultAggregationTypeLabel(GlobalAggregationSettings settings)
	{
		String defaultAggregationType = settings.getDefaultAggregationType().getLabel();
		add(new Label("defaultAggregationType", defaultAggregationType));
	}
	
	private void addDefaultMultivalueTypeLabel(GlobalAggregationSettings settings)
	{
		String defaultMultivalueType = settings.getDefaultMultivalueType().getLabel();
		add(new Label("defaultMultivalueType", defaultMultivalueType));
	}
	
	private void addDefaultErrorStrategy(GlobalAggregationSettings settings)
	{
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
				
				addPropertyLabel(item);
				addMultivalueTypeLabel(item, property);
				addAggregationTypeLabel(item, property);			
				
				addDeleteButton(item, property);
			}
		};
		
		add(listView);
	}
	
	private void addPropertyLabel(ListItem<PropertySettings> item)
	{
		item.add(new Label("property"));
	}
	
	private void addMultivalueTypeLabel(ListItem<PropertySettings> item, 
		PropertySettings property)
	{
		String multivalueType = property.getMultivalueType().getLabel();
		item.add(new Label("multivalueType", multivalueType));
	}
	
	private void addAggregationTypeLabel(ListItem<PropertySettings> item,
		PropertySettings property)
	{
		String aggregationType = property.getAggregationType().getLabel();
		item.add(new Label("aggregationType", aggregationType));
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
        
        button.add(new ConfirmationBoxRenderer("Are you sure you want to delete the property?"));
        
		item.add(button);
	}
}
