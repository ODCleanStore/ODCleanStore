package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import java.util.List;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.AggregationTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.PropertySettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class RegisterPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private PropertySettingsDao propertySettingsDao;
	private AggregationTypeDao aggregationTypeDao;
	
	public RegisterPropertyPage() 
	{
		super(
			"Home > OutputWS > CR > Aggregation Settings > Register", 
			"Register a new property"
		);

		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getPropertySettingsDao();
		aggregationTypeDao = daoLookupFactory.getAggregationTypeDao();
		
		// register page components
		//
		addNewPropertyForm();
	}
	
	private void addNewPropertyForm()
	{
		IModel formModel = new CompoundPropertyModel<PropertySettings>(new PropertySettings());
		
		Form<PropertySettings> newPropertyForm = new Form<PropertySettings>("newPropertyForm", formModel)
		{
			@Override
			protected void onSubmit()
			{
				PropertySettings propertySettings = this.getModelObject();
				
				propertySettingsDao.save(propertySettings);
				
				getSession().info("The property was successfuly registered.");
				setResponsePage(AggregationSettingsPage.class);
			}
		};
		
		addPropertyTextField(newPropertyForm);
		addMultivalueCheckbox(newPropertyForm);
		addAggregationTypeSelectBox(newPropertyForm);
		
		add(newPropertyForm);
	}
	
	private void addPropertyTextField(Form<PropertySettings> form)
	{
		TextField<String> textField = new TextField<String>("property");
		textField.setRequired(true);
		
		form.add(textField);
	}
	
	private void addMultivalueCheckbox(Form<PropertySettings> form)
	{
		CheckBox checkBox = new CheckBox("multivalue");
		form.add(checkBox);
	}
	
	private void addAggregationTypeSelectBox(Form<PropertySettings> form)
	{
		List<AggregationType> allAggregationTypes = aggregationTypeDao.loadAll();
		
		ChoiceRenderer renderer = new ChoiceRenderer("label", "id");
		
		DropDownChoice selectBox = new DropDownChoice<AggregationType>
		(
			"aggregationType",
			allAggregationTypes,
			renderer
		);
		
		selectBox.setRequired(true);
		
		form.add(selectBox);
	}
}
