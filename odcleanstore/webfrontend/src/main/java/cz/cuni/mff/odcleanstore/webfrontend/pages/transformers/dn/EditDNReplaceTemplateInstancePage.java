package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "PIC" })
public class EditDNReplaceTemplateInstancePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNReplaceTemplateInstancePage.class);
	
	private DaoForEntityWithSurrogateKey<DNReplaceTemplateInstance> dnReplaceTemplateInstanceDao;
	
	public EditDNReplaceTemplateInstancePage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Replace template instances > Edit", 
			"Edit a DN replace template instance"
		);
		
		// prepare DAO objects
		//
		this.dnReplaceTemplateInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNReplaceTemplateInstanceDao.class);
		
		// register page components
		//
		addHelpWindow(new DNRuleHelpPanel("content"));
		
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

	private void addEditDNReplaceTemplateInstanceForm(final DNReplaceTemplateInstance instance)
	{
		IModel<DNReplaceTemplateInstance> formModel = new CompoundPropertyModel<DNReplaceTemplateInstance>(instance);
		
		Form<DNReplaceTemplateInstance> form = new Form<DNReplaceTemplateInstance>("editDNReplaceTemplateInstanceForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNReplaceTemplateInstance instance = this.getModelObject();
				
				try {
					dnReplaceTemplateInstanceDao.update(instance);
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
						"The replace template instance could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The replace template instance was successfuly updated.");
				setResponsePage(new DNGroupDetailPage(instance.getGroupId()));
			}
		};
		
		form.add(createTextfield("propertyName"));
		form.add(createTextfield("pattern"));
		form.add(createTextfield("replacement"));
		
		add(form);
	}
}
