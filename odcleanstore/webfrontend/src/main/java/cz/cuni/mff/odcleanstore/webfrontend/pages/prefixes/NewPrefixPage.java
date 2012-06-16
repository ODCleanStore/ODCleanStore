package cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes.PrefixMapping;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.prefixes.PrefixMappingDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

public class NewPrefixPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<PrefixMapping> prefixMappingDao;
	
	public NewPrefixPage() 
	{
		super
		(
			"Home > Prefixes > Create", 
			"Register a new transformer"
		);
		

		// prepare DAO objects
		//
		prefixMappingDao = daoLookupFactory.getDao(PrefixMappingDao.class);
		
		// register page components
		//
		addNewPrefixForm();
	}
	
	private void addNewPrefixForm()
	{
		IModel<PrefixMapping> formModel = new CompoundPropertyModel<PrefixMapping>(new PrefixMapping());
		
		Form<PrefixMapping> form = new Form<PrefixMapping>("newPrefixForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				PrefixMapping mapping = this.getModelObject();
				
				try {
					prefixMappingDao.save(mapping);
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
						"The prefix could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The prefix was successfuly registered.");
				setResponsePage(PrefixesManagementPage.class);
			}
		};
		
		form.add(createTextfield("prefix"));
		form.add(createURITextField());
		
		add(form);
	}
	
	private TextField<String> createURITextField()
	{
		TextField<String> textField = createTextfield("url");
		textField.add(new IRIValidator());
		return textField;
	}
}
