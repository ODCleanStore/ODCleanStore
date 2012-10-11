package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;


import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNConcatenateTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNConcatenateTemplateInstanceCompiler;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.CompiledDNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNConcatenateTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ "PIC" })
public class EditDNConcatenateTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNConcatenateTemplateInstancePage.class);
	
	private DNConcatenateTemplateInstanceDao dnConcatenateTemplateInstanceDao;
	private CompiledDNRuleDao compiledDNRuleDao;
	
	public EditDNConcatenateTemplateInstancePage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Concatenate template instances > Edit", 
			"Edit a DN concatenate template instance",
			DNConcatenateTemplateInstanceDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		this.dnConcatenateTemplateInstanceDao = daoLookupFactory.getDao(DNConcatenateTemplateInstanceDao.class);
		this.compiledDNRuleDao = daoLookupFactory.getDao(CompiledDNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNConcatenateTemplateInstanceHelpPanel("content"));
		
		DNConcatenateTemplateInstance instance = dnConcatenateTemplateInstanceDao.load(ruleId);
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class,
				instance.getGroupId(), 
				"manageGroupRules"
			)
		);
		
		addEditDNConcatenateTemplateInstanceForm(instance);
	}

	private void addEditDNConcatenateTemplateInstanceForm(final DNConcatenateTemplateInstance instance)
	{
		IModel<DNConcatenateTemplateInstance> formModel = new CompoundPropertyModel<DNConcatenateTemplateInstance>(instance);
		
		Form<DNConcatenateTemplateInstance> form = new LimitedEditingForm<DNConcatenateTemplateInstance>("editDNConcatenateTemplateInstanceForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				DNConcatenateTemplateInstance instance = this.getModelObject();

				CompiledDNRule compiledRule = DNConcatenateTemplateInstanceCompiler.compile(instance);
							
				try {
					compiledDNRuleDao.delete(instance.getRawRuleId());
					int rawRuleId = compiledDNRuleDao.saveAndGetKey(compiledRule);
					
					instance.setRawRuleId(rawRuleId);

					// note that when deleting the raw rule, the template instance
					// gets automatically deleted as well, due to on delete constraints;
					// it is therefore necessary to insert the template instance (not update)
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
						"The concatenate template instance could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The concatenate template instance was successfuly updated.");
				setResponsePage(new DNGroupDetailPage(instance.getGroupId()));
			}
		};
		
		form.add(createIRITextfield("propertyName"));
		form.add(createTextfield("delimiter"));
		
		add(form);
	}
}