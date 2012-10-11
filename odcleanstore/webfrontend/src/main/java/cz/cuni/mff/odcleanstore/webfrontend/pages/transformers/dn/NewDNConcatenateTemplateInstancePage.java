package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNConcatenateTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNConcatenateTemplateInstanceCompiler;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.CompiledDNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNConcatenateTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ "PIC" })
public class NewDNConcatenateTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNConcatenateTemplateInstancePage.class);
	
	private DNConcatenateTemplateInstanceDao dnConcatenateTemplateInstanceDao;
	private CompiledDNRuleDao compiledDNRuleDao;
	
	public NewDNConcatenateTemplateInstancePage(final Integer groupId) 
	{
		super(
			"Home > Backend > DN > Groups > Concatenate template instances > New", 
			"Add a new DN concatenate template instance",
			DNRulesGroupDao.class,
			groupId
		);
		
		checkUnathorizedInstantiation();
		
		// prepare DAO objects
		//
		this.dnConcatenateTemplateInstanceDao = daoLookupFactory.getDao(DNConcatenateTemplateInstanceDao.class);
		this.compiledDNRuleDao = daoLookupFactory.getDao(CompiledDNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNConcatenateTemplateInstanceHelpPanel("content"));
		
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
		IModel<DNConcatenateTemplateInstance> formModel = new CompoundPropertyModel<DNConcatenateTemplateInstance>(
			new DNConcatenateTemplateInstance()
		);
		
		Form<DNConcatenateTemplateInstance> form = new Form<DNConcatenateTemplateInstance>("newDNConcatenateTemplateInstanceForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNConcatenateTemplateInstance instance = this.getModelObject();
				instance.setGroupId(groupId);
				
				CompiledDNRule compiledRule = DNConcatenateTemplateInstanceCompiler.compile(instance);
				
				try 
				{
					int rawRuleId = compiledDNRuleDao.saveAndGetKey(compiledRule);
					
					instance.setRawRuleId(rawRuleId);
					dnConcatenateTemplateInstanceDao.save(instance);
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
						"The concatenate template instance could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The concatenate template instance was successfuly registered.");
				setResponsePage(new DNGroupDetailPage(groupId));
			}
		};
		
		form.add(createIRITextfield("propertyName"));
		form.add(createTextfield("delimiter"));
		
		add(form);
	}
}