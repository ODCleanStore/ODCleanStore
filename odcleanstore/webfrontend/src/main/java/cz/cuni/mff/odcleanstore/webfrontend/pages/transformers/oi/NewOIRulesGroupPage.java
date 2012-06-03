package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewOIRulesGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<OIRulesGroup> oiRulesGroupDao;
	
	public NewOIRulesGroupPage() 
	{
		super(
			"Home > Transformers > OI > Rules management > Rules groups > Create", 
			"Add a new rules group"
		);

		// prepare DAO objects
		//
		this.oiRulesGroupDao = daoLookupFactory.getDao(OIRulesGroupDao.class);
		
		// register page components
		//
		addNewOIRulesGroupForm();
	}
	
	private void addNewOIRulesGroupForm()
	{
		IModel<OIRulesGroup> formModel = new CompoundPropertyModel<OIRulesGroup>(new OIRulesGroup());
		
		Form<OIRulesGroup> newOIRulesGroupForm = new Form<OIRulesGroup>("newOIRulesGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				OIRulesGroup group = this.getModelObject();
				
				try {
					oiRulesGroupDao.save(group);
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
						"The group could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly registered.");
				setResponsePage(OIRulesManagementPage.class);
			}
		};
		
		addLabelTextfield(newOIRulesGroupForm);
		addDescriptionTextarea(newOIRulesGroupForm);
		
		add(newOIRulesGroupForm);
	}

	private void addLabelTextfield(Form<OIRulesGroup> form)
	{
		TextField<String> textfield = new TextField<String>("label");
		textfield.setRequired(true);
		form.add(textfield);
	}
	
	private void addDescriptionTextarea(Form<OIRulesGroup> form)
	{
		TextArea<String> textarea = new TextArea<String>("description");
		textarea.setRequired(true);
		form.add(textarea);
	}
}
