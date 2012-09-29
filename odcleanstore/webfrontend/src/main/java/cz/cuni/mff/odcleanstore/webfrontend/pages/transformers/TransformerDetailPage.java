package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.ADM })
public class TransformerDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(TransformerDetailPage.class);
	
	private TransformerDao transformerDao;
	
	public TransformerDetailPage(final Integer id) 
	{
		super
		(
			"Home > Backend > Transformers > Edit", 
			"Edit a transformer"
		);
		

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDao(TransformerDao.class);
		
		// register page components
		//
		addHelpWindow(new TransformerHelpPanel("content"));
		addEditTransformerForm(id);
	}
	
	private void addEditTransformerForm(final Integer id)
	{
		Transformer transformer = transformerDao.load(id);
		IModel<Transformer> formModel = new CompoundPropertyModel<Transformer>(transformer);
		
		Form<Transformer> form = new Form<Transformer>("editTransformerForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Transformer transformer = this.getModelObject();
				
				try {
					transformerDao.update(transformer);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					
					getSession().error(
						"The transformer could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The transformer was successfuly updated.");
				setResponsePage(TransformersListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextfield("workDirPath"));
		form.add(createTextarea("description", false));
		form.add(createTextfield("jarPath"));
		form.add(createTextfield("fullClassName"));
		
		add(form);
	}
}
