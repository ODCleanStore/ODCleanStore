package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EditDNRuleComponentPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<DNRuleComponent> dnRuleComponentDao;
	private DaoForEntityWithSurrogateKey<DNRuleComponentType> dnRuleComponentTypeDao;
	
	/**
	 * 
	 * @param ruleId
	 */
	public EditDNRuleComponentPage(Long ruleComponentId) 
	{
		super(
				"Home >DN > Rules groups > Group > Rules > Rule > Components > Create", 
				"Add a new DN rule component"
		);

		// prepare DAO objects
		//
		dnRuleComponentDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentDao.class);
		dnRuleComponentTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentTypeDao.class);
		
		// register page components
		//
		DNRuleComponent component = dnRuleComponentDao.load(ruleComponentId);
		
		add(
			new RedirectButton(
				DNRuleDetailPage.class,
				component.getRuleId(), 
				"showDNRuleDetailPage"
			)
		);
		
		addEditComponentForm(component);
	}
	
	private void addEditComponentForm(final DNRuleComponent component)
	{
		IModel<DNRuleComponent> formModel = new CompoundPropertyModel<DNRuleComponent>(component);
		
		Form<DNRuleComponent> form = new Form<DNRuleComponent>("editDNRuleComponentForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRuleComponent dnRuleComponent = this.getModelObject();
				
				try 
				{
					dnRuleComponentDao.update(dnRuleComponent);
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
						"The component could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The component was successfuly updated.");
				setResponsePage(new DNRuleDetailPage(component.getRuleId()));
			}
		};
		
		form.add(createEnumSelectbox(dnRuleComponentTypeDao, "type"));
		form.add(createTextarea("modification", false));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
