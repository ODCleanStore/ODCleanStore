package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.DNComponentValidator;

/**
 * Add-new-component-to-a-dn-rule page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewDNRuleComponentPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewDNRuleComponentPage.class);

	private DNRuleComponentDao dnRuleComponentDao;
	private DNRuleComponentTypeDao dnRuleComponentTypeDao;
	
	/**
	 * 
	 * @param ruleId
	 */
	public NewDNRuleComponentPage(Integer ruleId) 
	{
		super(
			"Home > Backend > Data Normalization > Groups > Rules > Components > New", 
			"Add a new Data Normalization rule component",
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
	
	/**
	 * 
	 * @param ruleId
	 */
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
		
		DropDownChoice<DNRuleComponentType> type= createEnumSelectbox(dnRuleComponentTypeDao, "type");
		form.add(type);

		TextArea<String> modification = new TextArea<String>("modification");
		modification.setRequired(true);
		modification.add(new DNComponentValidator(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials(), type));
		form.add(modification);
		
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
