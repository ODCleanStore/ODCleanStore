package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class EditDNGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<DNRulesGroup> dnRulesGroupDao;
	
	public EditDNGroupPage(final Long groupId) 
	{
		super(
			"Home > DN > Rules groups > Create", 
			"Add a new rules group"
		);

		// prepare DAO objects
		//
		this.dnRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRulesGroupDao.class);
		
		// register page components
		//
		addEditDNRulesGroupForm(groupId);
	}
	
	private void addEditDNRulesGroupForm(final Long groupId)
	{
		DNRulesGroup group = dnRulesGroupDao.load(groupId);
		IModel<DNRulesGroup> formModel = new CompoundPropertyModel<DNRulesGroup>(group);
		
		Form<DNRulesGroup> form = new Form<DNRulesGroup>("editDNGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRulesGroup group = this.getModelObject();
				
				try {
					dnRulesGroupDao.update(group);
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
						"The group could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly updated.");
				setResponsePage(DNGroupsListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
