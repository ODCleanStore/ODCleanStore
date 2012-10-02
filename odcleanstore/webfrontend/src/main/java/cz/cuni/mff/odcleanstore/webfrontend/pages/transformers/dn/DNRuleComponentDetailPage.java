package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ Role.PIC })
public class DNRuleComponentDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;

	private DNRuleComponentDao dnRuleComponentDao;
	private DNRuleComponentTypeDao dnRuleComponentTypeDao;
	
	/**
	 * 
	 * @param ruleId
	 */
	public DNRuleComponentDetailPage(Integer ruleComponentId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Components > Edit", 
			"Edit a DN rule component",
			DNRuleComponentDao.class,
			ruleComponentId
		);

		// prepare DAO objects
		//
		dnRuleComponentDao = daoLookupFactory.getDao(DNRuleComponentDao.class, isEditable());
		dnRuleComponentTypeDao = daoLookupFactory.getDao(DNRuleComponentTypeDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRuleComponentHelpPanel("content"));
		
		DNRuleComponent component = dnRuleComponentDao.load(ruleComponentId);
		
		add(
			new RedirectWithParamButton(
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
		
		Form<DNRuleComponent> form = new LimitedEditingForm<DNRuleComponent>("editDNRuleComponentForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
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
		form.add(createTextarea("modification", true));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
