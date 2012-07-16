package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class EditOIGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<OIRulesGroup> oiRulesGroupDao;
	
	public EditOIGroupPage(final Long groupId) 
	{
		super(
			"Home > OI > Rules groups > Create", 
			"Add a new rules group"
		);

		// prepare DAO objects
		//
		this.oiRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRulesGroupDao.class);
		
		// register page components
		//
		addEditOIRulesGroupForm(groupId);
	}
	
	private void addEditOIRulesGroupForm(final Long groupId)
	{
		OIRulesGroup group = oiRulesGroupDao.load(groupId);
		IModel<OIRulesGroup> formModel = new CompoundPropertyModel<OIRulesGroup>(group);
		
		Form<OIRulesGroup> form = new Form<OIRulesGroup>("editOIRulesGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				OIRulesGroup group = this.getModelObject();
				
				try {
					oiRulesGroupDao.update(group);
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
				setResponsePage(OIGroupsListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
