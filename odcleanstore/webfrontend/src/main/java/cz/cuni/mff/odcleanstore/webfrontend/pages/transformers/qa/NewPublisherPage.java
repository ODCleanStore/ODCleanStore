package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.PublisherDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewPublisherPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
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
		
		Form<Publisher> newPublisherForm = new Form<Publisher>("newPublisherForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Publisher publisher = this.getModelObject();
				
				publisherDao.save(publisher);
				
				getSession().info("The publisher was successfuly registered.");
				setResponsePage(QARulesManagement.class);
			}
		};
		
		addLabelTextfield(newPublisherForm);
		addURITextfield(newPublisherForm);
		
		add(newPublisherForm);
	}

	private void addLabelTextfield(Form<Publisher> form)
	{
		TextField<String> textfield = new TextField<String>("label");
		textfield.setRequired(true);
		form.add(textfield);
	}
	
	private void addURITextfield(Form<Publisher> form)
	{
		TextField<String> textfield = new TextField<String>("uri");
		textfield.setRequired(true);
		form.add(textfield);
	}
}
