package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.PublisherDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

public class NewPublisherPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewPublisherPage.class);
	
	private Dao<Publisher> publisherDao;

	public NewPublisherPage() 
	{
		super(
			"Home > Transformers > QA > Rules management > Publishers > Create", 
			"Add a new publisher"
		);
		
		// prepare DAO objects
		//
		this.publisherDao = daoLookupFactory.getDao(PublisherDao.class);
		
		// register page components
		//
		addNewPublisherForm();
	}

	/*
	 	=======================================================================
	 	Implementace newPublisherForm
	 	=======================================================================
	*/
	
	private void addNewPublisherForm()
	{
		IModel<Publisher> formModel = new CompoundPropertyModel<Publisher>(new Publisher());
		
		Form<Publisher> form = new Form<Publisher>("newPublisherForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Publisher publisher = this.getModelObject();
				
				try {
					publisherDao.save(publisher);
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
						"The publisher could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The publisher was successfuly registered.");
				setResponsePage(QARulesManagementPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		addURITextfield(form);
		
		add(form);
	}

	private void addURITextfield(Form<Publisher> form)
	{
		TextField<String> textfield = createTextfield("uri");
		textfield.add(new IRIValidator());
		form.add(textfield);
	}
}
