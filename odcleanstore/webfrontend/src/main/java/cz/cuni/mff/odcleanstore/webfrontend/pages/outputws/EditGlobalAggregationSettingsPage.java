package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EditGlobalAggregationSettingsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private GlobalAggregationSettingsDao globalAggregationSettingsDao;
	private Dao<AggregationType> aggregationTypeDao;
	private Dao<MultivalueType> multivalueTypeDao;
	private Dao<ErrorStrategy> errorStrategyDao;
	
	public EditGlobalAggregationSettingsPage() 
	{
		super(
			"Home > OutputWS > CR > Aggregation Settings > Edit global settings", 
			"Edit global aggregation settings"
		);

		// prepare DAO objects
		//
		globalAggregationSettingsDao = daoLookupFactory.getGlobalAggregationSettingsDao();
		aggregationTypeDao = daoLookupFactory.getDao(AggregationTypeDao.class);
		multivalueTypeDao = daoLookupFactory.getDao(MultivalueTypeDao.class);
		errorStrategyDao = daoLookupFactory.getDao(ErrorStrategyDao.class);
		
		// register page components
		//
		addEditGlobalSettingsForm();
	}
	
	private void addEditGlobalSettingsForm()
	{
		IModel<GlobalAggregationSettings> formModel = new CompoundPropertyModel<GlobalAggregationSettings>(
			new GlobalAggregationSettings()
		);
		
		Form<GlobalAggregationSettings> form = new Form<GlobalAggregationSettings>("editGlobalSettingsForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				GlobalAggregationSettings settings = this.getModelObject();
				
				globalAggregationSettingsDao.save(settings);
				
				getSession().info("The global settings were successfuly altered.");
				setResponsePage(AggregationSettingsPage.class);
			}
		};

		form.add(createEnumSelectbox(multivalueTypeDao, "defaultMultivalueType"));
		form.add(createEnumSelectbox(aggregationTypeDao, "defaultAggregationType"));
		form.add(createEnumSelectbox(errorStrategyDao, "defaultErrorStrategy"));

		add(form);
	}
}
