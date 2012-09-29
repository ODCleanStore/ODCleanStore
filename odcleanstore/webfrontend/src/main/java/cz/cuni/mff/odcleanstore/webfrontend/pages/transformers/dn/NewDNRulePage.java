package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class NewDNRulePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private DNRuleDao dnRuleDao;
	
	public NewDNRulePage(final Integer groupId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > New", 
			"Add a new DN rule"
		);
		
		// prepare DAO objects
		//
		this.dnRuleDao = daoLookupFactory.getDao(DNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRuleHelpPanel("content"));
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class,
				groupId, 
				"manageGroupRules"
			)
		);
		
		addNewDNRuleForm(groupId);
	}

	private void addNewDNRuleForm(final Integer groupId)
	{
		IModel<DNRule> formModel = new CompoundPropertyModel<DNRule>(new DNRule());
		
		Form<DNRule> form = new Form<DNRule>("newDNRuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRule rule = this.getModelObject();
				rule.setGroupId(groupId);
				
				int insertId;
				try {
					insertId = dnRuleDao.saveAndGetKey(rule);
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
						"The rule could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly registered.");
				setResponsePage(new DNRuleDetailPage(insertId));
			}
		};
		
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
