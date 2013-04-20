package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Rename-dn-template-instance-overview page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ "PIC" })
public class EditDNRenameTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNReplaceTemplateInstancePage.class);
	
	private DNRenameTemplateInstanceDao dnRenameTemplateInstanceDao;
	private DNRuleDao dnRuleDao;
	
	/**
	 * 
	 * @param ruleId
	 */
	public EditDNRenameTemplateInstancePage(final Integer ruleId) 
	{
		super(
			"Home > Backend > Data Normalization > Groups > Rename template instances > Edit", 
			"Edit a Data Normalization rename template instance",
			DNRenameTemplateInstanceDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		this.dnRenameTemplateInstanceDao = daoLookupFactory.getDao(DNRenameTemplateInstanceDao.class);
		this.dnRuleDao = daoLookupFactory.getDao(DNRuleDao.class, true);
		
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

	/**
	 * 
	 * @param instance
	 */
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
					dnRenameTemplateInstanceDao.save(instance);
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
						"The rename template instance could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rename template instance was successfuly updated.");
				setResponsePage(new DNGroupDetailPage(instance.getGroupId()));
			}
		};
		
		form.add(createIRITextfield("sourcePropertyName"));
		form.add(createIRITextfield("targetPropertyName"));
		
		add(form);
	}
}
