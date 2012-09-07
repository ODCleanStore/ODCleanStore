package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "POC" })
public class NewTransformerPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;
	
	public NewTransformerPage() 
	{
		super
		(
			"Home > Backend > Transformers > New", 
			"Register a new transformer"
		);
		

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		
		// register page components
		//
		addHelpWindow(new TransformerHelpPanel("content"));
		addNewTransformerForm();
	}
	
	private void addNewTransformerForm()
	{
		IModel<Transformer> formModel = new CompoundPropertyModel<Transformer>(new Transformer());
		
		Form<Transformer> form = new Form<Transformer>("newTransformerForm", formModel)
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
				setResponsePage(TransformersListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		form.add(createTextfield("jarPath"));
		form.add(createTextfield("fullClassName"));
		
		add(form);
	}
}
