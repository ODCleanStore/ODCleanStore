package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class EditPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(EditPropertyPage.class);
	
	private DaoForEntityWithSurrogateKey<PropertySettings> propertySettingsDao;
	private DaoForEntityWithSurrogateKey<AggregationType> aggregationTypeDao;
	private DaoForEntityWithSurrogateKey<MultivalueType> multivalueTypeDao;

	public EditPropertyPage(final Long propertyId) 
	{
		super(
			"Home > Output WS > Aggregation Properties > Edit", 
			"Edit a property"
		);

		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PropertySettingsDao.class);
		aggregationTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(AggregationTypeDao.class);
		multivalueTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(MultivalueTypeDao.class);
		
		// register page components
		//
		addEditPropertyForm(propertyId);
	}
	
	private void addEditPropertyForm(final Long propertyId)
	{
		PropertySettings property = propertySettingsDao.load(propertyId);
		
		IModel<PropertySettings> formModel = new CompoundPropertyModel<PropertySettings>(property);
		
		Form<PropertySettings> form = new Form<PropertySettings>("editPropertyForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				PropertySettings propertySettings = this.getModelObject();
				
				try {
					propertySettingsDao.update(propertySettings);
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
						"The property could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The property was successfuly updated.");
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
