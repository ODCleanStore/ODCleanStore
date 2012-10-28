package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qe.LabelProperty;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qe.LabelPropertyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

/**
 * Configure-new-label-property page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC, Role.ADM })
public class NewQELabelPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewQELabelPropertyPage.class);

	private LabelPropertyDao labelPropertyDao;
	
	/**
	 * 
	 */
	public NewQELabelPropertyPage() 
	{
		super(
			"Home > Output WS > Label Properties > New", 
			"Add a new label property"
		);
		

		// prepare DAO objects
		//
		labelPropertyDao = daoLookupFactory.getDao(LabelPropertyDao.class);
		
		// register page components
		//
		addHelpWindow(new LabelPropertyHelpPanel("content"));
		addNewPropertyForm();
	}
	
	/**
	 * 
	 */
	private void addNewPropertyForm()
	{
		IModel<LabelProperty> formModel = new CompoundPropertyModel<LabelProperty>(new LabelProperty());
		
		Form<LabelProperty> form = new Form<LabelProperty>("newPropertyForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				LabelProperty property = this.getModelObject();
				
				try {
					labelPropertyDao.save(property);
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
						"The label property could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The label property was successfuly registered.");
				setResponsePage(QELabelPropertiesListPage.class);
			}
		};
		
		form.add(createPropertyTextField());
		
		add(form);
	}
	
	/**
	 * 
	 * @return
	 */
	private TextField<String> createPropertyTextField()
	{
		TextField<String> textField = createTextfield("property");
		textField.add(new IRIValidator());
		return textField;
	}
}
