package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qe.LabelProperty;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qe.LabelPropertyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewQELabelPropertyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<LabelProperty> labelPropertyDao;
	
	public NewQELabelPropertyPage() 
	{
		super(
			"Home > Output WS > CR > Label properties > Create", 
			"Register a new label property"
		);
		

		// prepare DAO objects
		//
		labelPropertyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(LabelPropertyDao.class);
		
		// register page components
		//
		addNewPropertyForm();
	}
	
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
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
					getSession().error(
						"The label property could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The label property was successfuly registered.");
				setResponsePage(QELabelPropertiesListPage.class);
			}
		};
		
		form.add(createTextfield("property"));
		
		add(form);
	}
}
