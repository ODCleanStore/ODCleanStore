package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RegexField;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.ReplacementField;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ "PIC" })
public class NewDNReplaceTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNReplaceTemplateInstancePage.class);
	
	private DNReplaceTemplateInstanceDao dnReplaceTemplateInstanceDao;
	//private CompiledDNRuleDao compiledDNRuleDao;
	
	public NewDNReplaceTemplateInstancePage(final Integer groupId) 
	{
		super(
			"Home > Backend > DN > Groups > Replace template instances > New", 
			"Add a new DN replace template instance",
			DNRulesGroupDao.class,
			groupId
		);
		
		checkUnathorizedInstantiation();
		
		// prepare DAO objects
		//
		this.dnReplaceTemplateInstanceDao = daoLookupFactory.getDao(DNReplaceTemplateInstanceDao.class);
		//this.compiledDNRuleDao = daoLookupFactory.getDao(CompiledDNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNReplaceTemplateInstanceHelpPanel("content"));
		
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
		IModel<DNReplaceTemplateInstance> formModel = new CompoundPropertyModel<DNReplaceTemplateInstance>(
			new DNReplaceTemplateInstance()
		);
		
		Form<DNReplaceTemplateInstance> form = new Form<DNReplaceTemplateInstance>("newDNReplaceTemplateInstanceForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNReplaceTemplateInstance instance = this.getModelObject();
				instance.setGroupId(groupId);
				
				try {
					dnReplaceTemplateInstanceDao.save(instance);
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
						"The replace template instance could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The replace template instance was successfuly registered.");
				setResponsePage(new DNGroupDetailPage(groupId));
			}
		};
		
		form.add(createIRITextfield("propertyName"));
		
		RegexField pattern = createRegexTextfield("pattern");
		form.add(pattern);
		
		ReplacementField replacement = createReplacementTextfield("replacement", pattern);
		form.add(replacement);
		
		add(form);
	}
}
