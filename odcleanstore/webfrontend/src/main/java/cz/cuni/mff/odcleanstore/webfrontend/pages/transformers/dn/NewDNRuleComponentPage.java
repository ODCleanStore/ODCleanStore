package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ Role.PIC })
public class NewDNRuleComponentPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewDNRuleComponentPage.class);

	private DNRuleComponentDao dnRuleComponentDao;
	private DNRuleComponentTypeDao dnRuleComponentTypeDao;
	
	public NewDNRuleComponentPage(Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Components > New", 
			"Add a new DN rule component",
			DNRuleDao.class,
			ruleId
		);
		
		checkUnathorizedInstantiation();

		// prepare DAO objects
		//
		dnRuleComponentDao = daoLookupFactory.getDao(DNRuleComponentDao.class, isEditable());
		dnRuleComponentTypeDao = daoLookupFactory.getDao(DNRuleComponentTypeDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRuleComponentHelpPanel("content"));
		
		add(
			new RedirectWithParamButton(
				DNRuleDetailPage.class,
				ruleId, 
				"showDNRuleDetailPage"
			)
		);
		
		addNewComponentForm(ruleId);
	}
	
	private void addNewComponentForm(final Integer ruleId)
	{
		IModel<DNRuleComponent> formModel = new CompoundPropertyModel<DNRuleComponent>(new DNRuleComponent());
		
		Form<DNRuleComponent> form = new Form<DNRuleComponent>("newDNRuleComponentForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRuleComponent dnRuleComponent = this.getModelObject();
				dnRuleComponent.setRuleId(ruleId);
				
				try {
					dnRuleComponentDao.save(dnRuleComponent);
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
						"The component could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The component was successfuly registered.");
				setResponsePage(new DNRuleDetailPage(ruleId));
			}
		};
		
		form.add(createEnumSelectbox(dnRuleComponentTypeDao, "type"));
		form.add(createTextarea("modification", true));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
