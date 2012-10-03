package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstanceCompiler;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstanceCompiler;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.CompiledDNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ "PIC" })
public class EditDNRenameTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNReplaceTemplateInstancePage.class);
	
	private DNRenameTemplateInstanceDao dnRenameTemplateInstanceDao;
	private CompiledDNRuleDao compiledDNRuleDao;
	
	public EditDNRenameTemplateInstancePage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rename template instances > Edit", 
			"Edit a DN rename template instance",
			DNRenameTemplateInstanceDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		this.dnRenameTemplateInstanceDao = daoLookupFactory.getDao(DNRenameTemplateInstanceDao.class);
		this.compiledDNRuleDao = daoLookupFactory.getDao(CompiledDNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRenameTemplateInstanceHelpPanel("content"));
		
		DNRenameTemplateInstance instance = dnRenameTemplateInstanceDao.load(ruleId);
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class,
				instance.getGroupId(), 
				"manageGroupRules"
			)
		);
		
		addEditDNRenameTemplateInstanceForm(instance);
	}

	private void addEditDNRenameTemplateInstanceForm(final DNRenameTemplateInstance instance)
	{
		IModel<DNRenameTemplateInstance> formModel = new CompoundPropertyModel<DNRenameTemplateInstance>(instance);
		
		Form<DNRenameTemplateInstance> form = new LimitedEditingForm<DNRenameTemplateInstance>("editDNRenameTemplateInstanceForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				DNRenameTemplateInstance instance = this.getModelObject();

				CompiledDNRule compiledRule = DNRenameTemplateInstanceCompiler.compile(instance);
				
				try 
				{
					compiledDNRuleDao.delete(instance.getRawRuleId());
					int rawRuleId = compiledDNRuleDao.saveAndGetKey(compiledRule);
					
					instance.setRawRuleId(rawRuleId);

					// note that when deleting the raw rule, the template instance
					// gets automatically deleted as well, due to on delete constraints;
					// it is therefore necessary to insert the template instance (not update)
					dnRenameTemplateInstanceDao.save(instance);
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
						"The rename template instance could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rename template instance was successfuly updated.");
				setResponsePage(new DNGroupDetailPage(instance.getGroupId()));
			}
		};
		
		form.add(createTextfield("sourcePropertyName"));
		form.add(createTextfield("targetPropertyName"));
		
		add(form);
	}
}
