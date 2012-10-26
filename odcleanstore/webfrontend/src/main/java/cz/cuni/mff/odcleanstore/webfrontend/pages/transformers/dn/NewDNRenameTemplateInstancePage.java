package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ "PIC" })
public class NewDNRenameTemplateInstancePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNRenameTemplateInstancePage.class);
	
	private DNRenameTemplateInstanceDao dnRenameTemplateInstanceDao;
	//private CompiledDNRuleDao compiledDNRuleDao;
	
	public NewDNRenameTemplateInstancePage(final Integer groupId) 
	{
		super(
			"Home > Backend > Data Normalization > Groups > Rename template instances > New", 
			"Add a new Data Normalization rename template instance",
			DNRulesGroupDao.class,
			groupId
		);
		
		checkUnathorizedInstantiation();
		
		// prepare DAO objects
		//
		this.dnRenameTemplateInstanceDao = daoLookupFactory.getDao(DNRenameTemplateInstanceDao.class);
		//this.compiledDNRuleDao = daoLookupFactory.getDao(CompiledDNRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRenameTemplateInstanceHelpPanel("content"));
		
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
		IModel<DNRenameTemplateInstance> formModel = new CompoundPropertyModel<DNRenameTemplateInstance>(
			new DNRenameTemplateInstance()
		);
		
		Form<DNRenameTemplateInstance> form = new Form<DNRenameTemplateInstance>("newDNRenameTemplateInstanceForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRenameTemplateInstance instance = this.getModelObject();
				instance.setGroupId(groupId);
		
				try {
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
						"The rename template instance could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rename template instance was successfuly registered.");
				setResponsePage(new DNGroupDetailPage(groupId));
			}
		};
		
		form.add(createIRITextfield("sourcePropertyName"));
		form.add(createIRITextfield("targetPropertyName"));
		
		add(form);
	}
}
