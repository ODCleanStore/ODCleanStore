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
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.DNComponentValidator;

@AuthorizeInstantiation({ Role.PIC })
public class DNRuleComponentDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(DNRuleComponentDetailPage.class);

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
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The component could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The component was successfuly updated.");
				setResponsePage(new DNRuleDetailPage(component.getRuleId()));
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
