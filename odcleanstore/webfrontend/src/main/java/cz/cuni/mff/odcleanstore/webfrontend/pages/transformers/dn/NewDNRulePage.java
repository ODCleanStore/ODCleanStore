package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Create-new-dn-rule page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewDNRulePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewDNRulePage.class);
	
	private DNRuleDao dnRuleDao;
	
	/**
	 * 
	 * @param groupId
	 */
	public NewDNRulePage(final Integer groupId) 
	{
		super(
			"Home > Backend > Data Normalization > Groups > Rules > New", 
			"Add a new Data Normalization rule",
			DNRulesGroupDao.class,
			groupId
		);
		
		checkUnathorizedInstantiation();
		
		// prepare DAO objects
		//
		this.dnRuleDao = daoLookupFactory.getDao(DNRuleDao.class, isEditable());
		
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

	/**
	 * 
	 * @param groupId
	 */
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
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The rule could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly registered.");
				setResponsePage(new DNRuleDetailPage(insertId));
			}
		};
		
		form.add(createTextfield("label"));
		
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
