package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EditGlobalAggregationSettingsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private Dao<GlobalAggregationSettings> globalAggregationSettingsDao;
	private DaoForEntityWithSurrogateKey<AggregationType> aggregationTypeDao;
	private DaoForEntityWithSurrogateKey<MultivalueType> multivalueTypeDao;
	private DaoForEntityWithSurrogateKey<ErrorStrategy> errorStrategyDao;
	
	public EditGlobalAggregationSettingsPage() 
	{
		super(
			"Home > Output WS > Aggregation Properties > Global > Edit", 
			"Edit global aggregation settings"
		);

		// prepare DAO objects
		//
		globalAggregationSettingsDao = daoLookupFactory.getDao(GlobalAggregationSettingsDao.class);
		aggregationTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(AggregationTypeDao.class);
		multivalueTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(MultivalueTypeDao.class);
		errorStrategyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(ErrorStrategyDao.class);
		
		// register page components
		//
		addEditGlobalSettingsForm();
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
				setResponsePage(CRPropertiesListPage.class);
			}
		};
		
		form.add(createEnumSelectbox(multivalueTypeDao, "defaultMultivalueType"));
		form.add(createEnumSelectbox(aggregationTypeDao, "defaultAggregationType"));
		form.add(createEnumSelectbox(errorStrategyDao, "defaultErrorStrategy"));
		
		add(form);
	}
}
