package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.ErrorStrategy;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.GlobalAggregationSettings;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.AggregationTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.ErrorStrategyDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.GlobalAggregationSettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.MultivalueTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

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
		List<MultivalueType> allMultivalueTypes = multivalueTypeDao.loadAll();
		
		ChoiceRenderer<MultivalueType> renderer = new ChoiceRenderer<MultivalueType>("label", "id");
		
		DropDownChoice<MultivalueType> selectBox = new DropDownChoice<MultivalueType>
		(
			"defaultMultivalueType",
			allMultivalueTypes,
			renderer
		);
		
		selectBox.setRequired(true);
		
		form.add(selectBox);
	}
	
	private void addDefaultAggregationTypeCheckBox(Form<GlobalAggregationSettings> form)
	{
		List<AggregationType> allAggregationTypes = aggregationTypeDao.loadAll();
		
		ChoiceRenderer<AggregationType> renderer = new ChoiceRenderer<AggregationType>("label", "id");
		
		DropDownChoice<AggregationType> selectBox = new DropDownChoice<AggregationType>
		(
			"defaultAggregationType",
			allAggregationTypes,
			renderer
		);
		
		selectBox.setRequired(true);
		
		form.add(selectBox);
	}

	private void addDefaultErrorStrategyCheckBox(Form<GlobalAggregationSettings> form)
	{
		List<ErrorStrategy> allErrorStrategies = errorStrategyDao.loadAll();
		
		ChoiceRenderer<ErrorStrategy> renderer = new ChoiceRenderer<ErrorStrategy>("label", "id");
		
		DropDownChoice<ErrorStrategy> selectBox = new DropDownChoice<ErrorStrategy>
		(
			"defaultErrorStrategy",
			allErrorStrategies,
			renderer
		);
		
		selectBox.setRequired(true);
		
		form.add(selectBox);
	}
}
