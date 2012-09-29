package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIRuleHelpPanel;

@AuthorizeInstantiation({ "PIC" })
public class NewDNRenameTemplateInstancePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNRenameTemplateInstancePage.class);
	
	private DaoForEntityWithSurrogateKey<DNRenameTemplateInstance> dnRenameTemplateInstanceDao;
	
	public NewDNRenameTemplateInstancePage(final Integer groupId) 
	{
		super(
			"Home > Backend > DN > Groups > Rename template instances > New", 
			"Add a new DN rename template instance"
		);
		
		// prepare DAO objects
		//
		this.dnRenameTemplateInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRenameTemplateInstanceDao.class);
		
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
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					
					getSession().error(
						"The rename template instance could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rename template instance was successfuly registered.");
				setResponsePage(new DNGroupDetailPage(groupId));
			}
		};
		
		form.add(createTextfield("sourcePropertyName"));
		form.add(createTextfield("targetPropertyName"));
		
		add(form);
	}
}