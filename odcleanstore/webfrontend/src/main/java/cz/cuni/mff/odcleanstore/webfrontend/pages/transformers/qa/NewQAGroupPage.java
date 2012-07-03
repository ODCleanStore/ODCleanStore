package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewQAGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<QARulesGroup> qaRulesGroupDao;
	
	public NewQAGroupPage() 
	{
		super(
			"Home > QA > Rules groups > Create", 
			"Add a new rules group"
		);

		// prepare DAO objects
		//
		this.qaRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(QARulesGroupDao.class);
		
		// register page components
		//
		addNewOIRulesGroupForm();
	}
	
	private void addNewOIRulesGroupForm()
	{
		IModel<QARulesGroup> formModel = new CompoundPropertyModel<QARulesGroup>(new QARulesGroup());
		
		Form<QARulesGroup> form = new Form<QARulesGroup>("newQAGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				QARulesGroup group = this.getModelObject();
				
				try {
					qaRulesGroupDao.save(group);
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
				setResponsePage(QAGroupsListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
