package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstanceCompiler;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.CompiledDNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNFilterTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ "PIC" })
public class NewDNFilterTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNFilterTemplateInstancePage.class);
	
	private DNFilterTemplateInstanceDao dnFilterTemplateInstanceDao;
	private CompiledDNRuleDao compiledDNRuleDao;
	
	public NewDNFilterTemplateInstancePage(final Integer groupId) 
	{
		super(
			"Home > Backend > DN > Groups > Filter template instances > New", 
			"Add a new DN filter template instance",
			DNRulesGroupDao.class,
			groupId
		);
		
		checkUnathorizedInstantiation();
		
		// prepare DAO objects
		//
		this.dnFilterTemplateInstanceDao = daoLookupFactory.getDao(DNFilterTemplateInstanceDao.class);
		this.compiledDNRuleDao = daoLookupFactory.getDao(CompiledDNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNFilterTemplateInstanceHelpPanel("content"));
		
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
		IModel<DNFilterTemplateInstance> formModel = new CompoundPropertyModel<DNFilterTemplateInstance>(
			new DNFilterTemplateInstance()
		);
		
		Form<DNFilterTemplateInstance> form = new Form<DNFilterTemplateInstance>("newDNFilterTemplateInstanceForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNFilterTemplateInstance instance = this.getModelObject();
				instance.setGroupId(groupId);
				
				CompiledDNRule compiledRule = DNFilterTemplateInstanceCompiler.compile(instance);
				
				try 
				{
					int rawRuleId = compiledDNRuleDao.saveAndGetKey(compiledRule);
					
					instance.setRawRuleId(rawRuleId);
					dnFilterTemplateInstanceDao.save(instance);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					
					getSession().error(
						"The filter template instance could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The filter template instance was successfuly registered.");
				setResponsePage(new DNGroupDetailPage(groupId));
			}
		};
		
		form.add(createTextfield("propertyName"));
		form.add(createTextfield("pattern"));
		form.add(createCheckbox("keep"));
		
		add(form);
	}
}
