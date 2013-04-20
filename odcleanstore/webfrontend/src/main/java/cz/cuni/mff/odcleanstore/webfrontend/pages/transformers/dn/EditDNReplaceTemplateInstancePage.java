package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RegexField;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.ReplacementField;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Replace-dn-template-instance-overview page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ "PIC" })
public class EditDNReplaceTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNReplaceTemplateInstancePage.class);
	
	private DNReplaceTemplateInstanceDao dnReplaceTemplateInstanceDao;
	private DNRuleDao dnRuleDao;
	
	/**
	 * 
	 * @param ruleId
	 */
	public EditDNReplaceTemplateInstancePage(final Integer ruleId) 
	{
		super(
			"Home > Backend > Data Normalization > Groups > Replace template instances > Edit", 
			"Edit a Data Normalization replace template instance",
			DNReplaceTemplateInstanceDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		this.dnReplaceTemplateInstanceDao = daoLookupFactory.getDao(DNReplaceTemplateInstanceDao.class);
		this.dnRuleDao = daoLookupFactory.getDao(DNRuleDao.class, true);
		
		// register page components
		//
		addHelpWindow(new DNReplaceTemplateInstanceHelpPanel("content"));
		
		DNReplaceTemplateInstance instance = dnReplaceTemplateInstanceDao.load(ruleId);
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class,
				instance.getGroupId(), 
				"manageGroupRules"
			)
		);
		
		addEditDNReplaceTemplateInstanceForm(instance);
	}

	/**
	 * 
	 * @param instance
	 */
	private void addEditDNReplaceTemplateInstanceForm(final DNReplaceTemplateInstance instance)
	{
		IModel<DNReplaceTemplateInstance> formModel = new CompoundPropertyModel<DNReplaceTemplateInstance>(instance);
		
		Form<DNReplaceTemplateInstance> form = new LimitedEditingForm<DNReplaceTemplateInstance>("editDNReplaceTemplateInstanceForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				DNReplaceTemplateInstance instance = this.getModelObject();
				
				try 
				{
					String oldRawLabel = dnRuleDao.load(instance.getRawRuleId()).getLabel();
					instance.setLabel(oldRawLabel);

					String oldRawDescription = dnRuleDao.load(instance.getRawRuleId()).getDescription();
					instance.setDescription(oldRawDescription);
  
					dnRuleDao.delete(instance.getRawRuleId());
					
					// note that when deleting the raw rule, the template instance
					// gets automatically deleted as well, due to on delete constraints;
					// it is therefore necessary to insert the template instance (not update)
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
						"The replace template instance could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The replace template instance was successfuly updated.");
				setResponsePage(new DNGroupDetailPage(instance.getGroupId()));
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
