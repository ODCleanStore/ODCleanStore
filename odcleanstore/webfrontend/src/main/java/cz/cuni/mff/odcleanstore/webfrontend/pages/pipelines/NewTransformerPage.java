package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewTransformerPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<Transformer> transformerDao;
	
	public NewTransformerPage() 
	{
		super
		(
			"Home > Pipelines > Transformers > Create", 
			"Register a new transformer"
		);
		

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDao(TransformerDao.class);
		
		// register page components
		//
		addNewTransformerForm();
	}
	
	private void addNewTransformerForm()
	{
		IModel<Transformer> formModel = new CompoundPropertyModel<Transformer>(new Transformer());
		
		Form<Transformer> newTransformerForm = new Form<Transformer>("newTransformerForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Transformer transformer = this.getModelObject();
				
				try {
					transformerDao.save(transformer);
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
						"The transformer could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The transformer was successfuly registered.");
				setResponsePage(TransformersManagementPage.class);
			}
		};
		
		addLabelTextfield(newTransformerForm);
		addDescriptionTextarea(newTransformerForm);
		addJarPathTextfield(newTransformerForm);
		addFullClassNameTextfield(newTransformerForm);
		
		add(newTransformerForm);
	}
	
	private void addLabelTextfield(Form<Transformer> form)
	{
		TextField<String> textfield = new TextField<String>("label");
		
		textfield.setRequired(true);
		
		form.add(textfield);
	}
	
	private void addDescriptionTextarea(Form<Transformer> form)
	{
		TextArea<String> textarea = new TextArea<String>("description");
		textarea.setRequired(true);
		form.add(textarea);
	}
	
	private void addJarPathTextfield(Form<Transformer> form)
	{
		TextField<String> textfield = new TextField<String>("jarPath");
		
		textfield.setRequired(true);
		
		form.add(textfield);
	}
	
	private void addFullClassNameTextfield(Form<Transformer> form)
	{
		TextField<String> textfield = new TextField<String>("fullClassName");
		
		textfield.setRequired(true);
		
		form.add(textfield);
	}
}
