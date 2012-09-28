package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.AggregationTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.ErrorStrategyDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.GlobalAggregationSettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.MultivalueTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.PropertySettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class AggregationSettingsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(AggregationSettingsPage.class);
	
	private DaoForEntityWithSurrogateKey<PropertySettings> propertySettingsDao;
	private Dao<GlobalAggregationSettings> globalAggregationSettingsDao;
	private DaoForEntityWithSurrogateKey<AggregationType> aggregationTypeDao;
	private DaoForEntityWithSurrogateKey<MultivalueType> multivalueTypeDao;
	private DaoForEntityWithSurrogateKey<ErrorStrategy> errorStrategyDao;
	
	public AggregationSettingsPage() 
	{
		super(
			"Home > Output WS > Aggregation Properties", 
			"Output WS aggregation settings"
		);
		
		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PropertySettingsDao.class);
		globalAggregationSettingsDao = daoLookupFactory.getDao(GlobalAggregationSettingsDao.class);
		aggregationTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(AggregationTypeDao.class);
		multivalueTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(MultivalueTypeDao.class);
		errorStrategyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(ErrorStrategyDao.class);
		
		// register page components
		//
		addHelpWindow(new AggregationPropertyHelpPanel("content"));
		addEditGlobalSettingsForm();
		addPropertySettingsTable();
	}

	private void addEditGlobalSettingsForm()
	{
		IModel<GlobalAggregationSettings> formModel = new CompoundPropertyModel<GlobalAggregationSettings>(
			globalAggregationSettingsDao.loadFirstRaw()
		);
		
		Form<GlobalAggregationSettings> form = new Form<GlobalAggregationSettings>("editGlobalSettingsForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				GlobalAggregationSettings settings = this.getModelObject();
				
				try {
					globalAggregationSettingsDao.save(settings);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception e) 
				{
					getSession().error("Could not save global settings due to an unexpected error");
					return;
				}
				
				getSession().info("The global settings were successfuly altered.");
				setResponsePage(AggregationSettingsPage.class);
			}
		};
		
		form.add(createEnumSelectbox(multivalueTypeDao, "defaultMultivalueType"));
		form.add(createEnumSelectbox(aggregationTypeDao, "defaultAggregationType"));
		form.add(createEnumSelectbox(errorStrategyDao, "defaultErrorStrategy"));
		
		add(form);
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
		SortableDataProvider<PropertySettings> data = new GenericSortableDataProvider<PropertySettings>
		(
			propertySettingsDao, 
			"property"
		);
			
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
					new DeleteRawButton<PropertySettings>(
						propertySettingsDao, 
						property.getId(), 
						"property", 
						new DeleteConfirmationMessage("property"),  
						AggregationSettingsPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						EditPropertyPage.class, 
						property.getId(), 
						"showEditGlobalAggregationSettingsPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<PropertySettings>("sortByProperty", "property", data, dataView));
		add(new SortTableButton<PropertySettings>("sortByMultivalueTypeId", "mtid", data, dataView));
		add(new SortTableButton<PropertySettings>("sortByAggregationTypeId", "atid", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
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
