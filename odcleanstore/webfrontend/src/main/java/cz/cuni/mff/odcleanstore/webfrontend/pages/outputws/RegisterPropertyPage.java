package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import java.util.List;

import javax.swing.text.MutableAttributeSet;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.AggregationType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.MultivalueType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.AggregationTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.MultivalueTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.PropertySettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class RegisterPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private PropertySettingsDao propertySettingsDao;
	private AggregationTypeDao aggregationTypeDao;
	private MultivalueTypeDao multivalueTypeDao;
	
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
		multivalueTypeDao = daoLookupFactory.getMultivalueTypeDao();
		
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
		addMultivalueSelectBox(newPropertyForm);
		addAggregationTypeSelectBox(newPropertyForm);
		
		add(newPropertyForm);
	}
	
	private void addPropertyTextField(Form<PropertySettings> form)
	{
		TextField<String> textField = new TextField<String>("property");
		textField.setRequired(true);
		
		form.add(textField);
	}
	
	private void addMultivalueSelectBox(Form<PropertySettings> form)
	{
		List<MultivalueType> allMultivalueTypes = multivalueTypeDao.loadAll();
		
		ChoiceRenderer renderer = new ChoiceRenderer("label", "id");
		
		DropDownChoice selectBox = new DropDownChoice<MultivalueType>
		(
			"multivalueType",
			allMultivalueTypes,
			renderer
		);
		
		selectBox.setRequired(true);
		
		form.add(selectBox);
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
