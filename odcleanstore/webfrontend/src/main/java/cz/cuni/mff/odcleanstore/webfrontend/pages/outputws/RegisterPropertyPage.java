package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class RegisterPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<PropertySettings> propertySettingsDao;
	private Dao<AggregationType> aggregationTypeDao;
	private Dao<MultivalueType> multivalueTypeDao;
	
	public RegisterPropertyPage() 
	{
		super(
			"Home > OutputWS > CR > Aggregation Settings > Register property", 
			"Register a new property"
		);

		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getDao(PropertySettingsDao.class);
		aggregationTypeDao = daoLookupFactory.getDao(AggregationTypeDao.class);
		multivalueTypeDao = daoLookupFactory.getDao(MultivalueTypeDao.class);
		
		// register page components
		//
		addNewPropertyForm();
	}
	
	private void addNewPropertyForm()
	{
		IModel<PropertySettings> formModel = new CompoundPropertyModel<PropertySettings>(new PropertySettings());
		
		Form<PropertySettings> newPropertyForm = new Form<PropertySettings>("newPropertyForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				PropertySettings propertySettings = this.getModelObject();
				
				try {
					propertySettingsDao.save(propertySettings);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
					getSession().error(
						"The property could not be registered due to an unexpected error."
					);
					
					return;
				}
				
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
		textField.add(new IRIValidator());
		
		form.add(textField);
	}
	
	private void addMultivalueSelectBox(Form<PropertySettings> form)
	{
		DropDownChoice<MultivalueType> selectBox = createEnumSelectBox(
			multivalueTypeDao, "multivalueType"
		);
		
		form.add(selectBox);
	}
	
	private void addAggregationTypeSelectBox(Form<PropertySettings> form)
	{
		DropDownChoice<AggregationType> selectBox = createEnumSelectBox(
			aggregationTypeDao, "aggregationType"
		);
		
		form.add(selectBox);
	}
}
