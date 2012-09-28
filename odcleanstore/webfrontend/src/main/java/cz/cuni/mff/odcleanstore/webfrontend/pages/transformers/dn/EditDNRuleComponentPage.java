package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class EditDNRuleComponentPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<DNRuleComponent> dnRuleComponentDao;
	private DaoForEntityWithSurrogateKey<DNRuleComponentType> dnRuleComponentTypeDao;
	
	/**
	 * 
	 * @param ruleId
	 */
	public EditDNRuleComponentPage(Integer ruleComponentId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Components > Edit", 
			"Edit a DN rule component"
		);

		// prepare DAO objects
		//
		dnRuleComponentDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentDao.class);
		dnRuleComponentTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentTypeDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRuleComponentHelpPanel("content"));
		
		DNRuleComponent component = dnRuleComponentDao.load(ruleComponentId);
		
		add(
			new RedirectWithParamButton(
				EditDNRulePage.class,
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
				setResponsePage(new EditDNRulePage(component.getRuleId()));
			}
		};
		
		form.add(createEnumSelectbox(dnRuleComponentTypeDao, "type"));
		form.add(createTextarea("modification", true));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
