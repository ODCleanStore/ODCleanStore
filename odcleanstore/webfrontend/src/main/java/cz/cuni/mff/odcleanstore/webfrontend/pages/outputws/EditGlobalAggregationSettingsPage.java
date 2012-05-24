package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EditGlobalAggregationSettingsPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private GlobalAggregationSettingsDao globalAggregationSettingsDao;
	private AggregationTypeDao aggregationTypeDao;
	private MultivalueTypeDao multivalueTypeDao;
	private ErrorStrategyDao errorStrategyDao;
	
	public EditGlobalAggregationSettingsPage() 
	{
		super(
			"Home > OutputWS > CR > Aggregation Settings > Edit global settings", 
			"Edit global aggregation settings"
		);

		// prepare DAO objects
		//
		globalAggregationSettingsDao = daoLookupFactory.getGlobalAggregationSettingsDao();
		aggregationTypeDao = daoLookupFactory.getAggregationTypeDao();
		multivalueTypeDao = daoLookupFactory.getMultivalueTypeDao();
		errorStrategyDao = daoLookupFactory.getErrorStrategyDao();
		
		// register page components
		//
		addEditGlobalSettingsForm();
	}
	
	private void addEditGlobalSettingsForm()
	{
		IModel<GlobalAggregationSettings> formModel = new CompoundPropertyModel<GlobalAggregationSettings>(
			new GlobalAggregationSettings()
		);
		
		Form<GlobalAggregationSettings> editGlobalSettingsForm = new Form<GlobalAggregationSettings>("editGlobalSettingsForm", formModel)
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

		addDefaultMultivalueTypeCheckBox(editGlobalSettingsForm);
		addDefaultAggregationTypeCheckBox(editGlobalSettingsForm);
		addDefaultErrorStrategyCheckBox(editGlobalSettingsForm);
		
		add(editGlobalSettingsForm);
	}
	
	private void addDefaultMultivalueTypeCheckBox(Form<GlobalAggregationSettings> form)
	{
		DropDownChoice<MultivalueType> selectBox = createEnumSelectBox(
				multivalueTypeDao, "defaultMultivalueType"
		);
		
		form.add(selectBox);
	}
	
	private void addDefaultAggregationTypeCheckBox(Form<GlobalAggregationSettings> form)
	{
		DropDownChoice<AggregationType> selectBox = createEnumSelectBox(
			aggregationTypeDao, "defaultAggregationType"
		);
		
		form.add(selectBox);
	}

	private void addDefaultErrorStrategyCheckBox(Form<GlobalAggregationSettings> form)
	{
		DropDownChoice<ErrorStrategy> selectBox = createEnumSelectBox(
			errorStrategyDao, "defaultErrorStrategy"
		);
		
		form.add(selectBox);
	}
}
