package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.PropertySettings;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.AggregationTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.MultivalueTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.PropertySettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

/**
 * Configure-new-aggregation-property page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC, Role.ADM })
public class NewPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewPropertyPage.class);

	private PropertySettingsDao propertySettingsDao;
	private AggregationTypeDao aggregationTypeDao;
	private MultivalueTypeDao multivalueTypeDao;
	
	/**
	 * 
	 */
	public NewPropertyPage() 
	{
		super(
			"Home > Output WS > Aggregation Properties > New", 
			"Add a new property"
		);

		// prepare DAO objects
		//
		propertySettingsDao = daoLookupFactory.getDao(PropertySettingsDao.class);
		aggregationTypeDao = daoLookupFactory.getDao(AggregationTypeDao.class);
		multivalueTypeDao = daoLookupFactory.getDao(MultivalueTypeDao.class);
		
		// register page components
		//
		addHelpWindow(new AggregationPropertyHelpPanel("content"));
		addNewPropertyForm();
	}
	
	/**
	 * 
	 */
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
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The property could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The property was successfuly registered.");
				setResponsePage(AggregationSettingsPage.class);
			}
		};
		
		addPropertyTextField(form);
		form.add(createEnumSelectbox(multivalueTypeDao, "multivalueType"));
		form.add(createEnumSelectbox(aggregationTypeDao, "aggregationType"));
		
		add(form);
	}
	
	/**
	 * 
	 * @param form
	 */
	private void addPropertyTextField(Form<PropertySettings> form)
	{
		TextField<String> textField = createTextfield("property");
		textField.add(new IRIValidator());
		form.add(textField);
	}
}
