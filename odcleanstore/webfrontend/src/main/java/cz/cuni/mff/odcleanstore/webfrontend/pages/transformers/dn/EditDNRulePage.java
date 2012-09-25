package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class EditDNRulePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<DNRule> dnRuleDao;
	
	public EditDNRulePage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Edit", 
			"Edit a DN rule"
		);
		
		// prepare DAO objects
		//
		this.dnRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRuleHelpPanel("content"));
		
		DNRule rule = dnRuleDao.load(ruleId);
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class,
				rule.getGroupId(), 
				"manageGroupRules"
			)
		);
		
		addEditDNRuleForm(rule);
	}

	private void addEditDNRuleForm(final DNRule rule)
	{
		IModel<DNRule> formModel = new CompoundPropertyModel<DNRule>(rule);
		
		Form<DNRule> form = new Form<DNRule>("editDNRuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRule rule = this.getModelObject();
				
				try {
					dnRuleDao.update(rule);
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
						"The rule could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly updated.");
				setResponsePage(new DNGroupDetailPage(rule.getGroupId()));
			}
		};
		
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
