package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

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
		IModel<GlobalAggregationSettings> model = new LoadableDetachableModel<GlobalAggregationSettings>() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected GlobalAggregationSettings load() 
			{
				return globalAggregationSettingsDao.load();
			}
		};
		
		addDefaultAggregationTypeLabel(model.getObject());
		addDefaultMultivalueTypeLabel(model.getObject());
		addDefaultErrorStrategyLabel(model.getObject());
	}
	
	private void addDefaultAggregationTypeLabel(GlobalAggregationSettings settings)
	{
		AggregationType aggregationType = settings.getDefaultAggregationType();
		
		Label label = new Label("defaultAggregationType", aggregationType.getLabel());
		label.add(new AttributeModifier("title", new Model<String>(aggregationType.getDescription())));
		add(label);
	}
	
	private void addDefaultMultivalueTypeLabel(GlobalAggregationSettings settings)
	{
		MultivalueType multivalueType = settings.getDefaultMultivalueType();
		
		Label label = new Label("defaultMultivalueType", multivalueType.getLabel());
		label.add(new AttributeModifier("title", new Model<String>(multivalueType.getDescription())));
		
		add(label);
	}
	
	private void addDefaultErrorStrategyLabel(GlobalAggregationSettings settings)
	{
		ErrorStrategy errorStrategy = settings.getDefaultErrorStrategy();
		
		Label label = new Label("defaultErrorStrategy", errorStrategy.getLabel());
		label.add(new AttributeModifier("title", new Model<String>(errorStrategy.getDescription())));
		
		add(label);
	}
	
	private void addPropertySettingsTable()
	{
		IDataProvider<PropertySettings> data = new DataProvider<PropertySettings>(propertySettingsDao);
				
		DataView<PropertySettings> dataView = new DataView<PropertySettings>("propertySettingsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<PropertySettings> item) 
			{
				PropertySettings property = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<PropertySettings>(property));
				
				addPropertyLabel(item);
				addMultivalueTypeLabel(item, property);
				addAggregationTypeLabel(item, property);			
				
				item.add(
					createDeleteRawButton(
						propertySettingsDao, 
						property.getId(), 
						"deleteProperty", 
						"property", 
						AggregationSettingsPage.class
					)
				);	
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
	private void addPropertyLabel(Item<PropertySettings> item)
	{
		item.add(new Label("property"));
	}
	
	private void addMultivalueTypeLabel(Item<PropertySettings> item, 
		PropertySettings property)
	{
		MultivalueType multivalueType = property.getMultivalueType();
		
		Label label = new Label("multivalueType", multivalueType.getLabel());
		label.add(new AttributeModifier("title", new Model<String>(multivalueType.getDescription())));
		
		item.add(label);
	}
	
	private void addAggregationTypeLabel(Item<PropertySettings> item,
		PropertySettings property)
	{
		AggregationType aggregationType = property.getAggregationType();
		
		Label label = new Label("aggregationType", aggregationType.getLabel());
		label.add(new AttributeModifier("title", new Model<String>(aggregationType.getDescription())));
		
		item.add(label);
	}
}
