package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

@AuthorizeInstantiation({ "PIC" })
public class NewPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<PropertySettings> propertySettingsDao;
	private DaoForEntityWithSurrogateKey<AggregationType> aggregationTypeDao;
	private DaoForEntityWithSurrogateKey<MultivalueType> multivalueTypeDao;
	
	public NewPropertyPage() 
	{
		super(
			"Home > Output WS > Aggregation Properties > New", 
			"Add a new property"
		);

		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PropertySettingsDao.class);
		aggregationTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(AggregationTypeDao.class);
		multivalueTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(MultivalueTypeDao.class);
		
		// register page components
		//
		addHelpWindow(new AggregationPropertyHelpPanel("content"));
		addNewPropertyForm();
	}
	
	private void addNewPropertyForm()
	{
		IModel<PropertySettings> formModel = new CompoundPropertyModel<PropertySettings>(new PropertySettings());
		
		Form<PropertySettings> form = new Form<PropertySettings>("newPropertyForm", formModel)
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
				setResponsePage(CRPropertiesListPage.class);
			}
		};
		
		addPropertyTextField(form);
		form.add(createEnumSelectbox(multivalueTypeDao, "multivalueType"));
		form.add(createEnumSelectbox(aggregationTypeDao, "aggregationType"));
		
		add(form);
	}
	
	private void addPropertyTextField(Form<PropertySettings> form)
	{
		TextField<String> textField = createTextfield("property");
		textField.add(new IRIValidator());
		form.add(textField);
	}
}
