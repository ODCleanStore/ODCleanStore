package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNFilterTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRenameTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNReplaceTemplateInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "PIC" })
public class EditDNFilterTemplateInstancePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(NewDNReplaceTemplateInstancePage.class);
	
	private DaoForEntityWithSurrogateKey<DNFilterTemplateInstance> dnFilterTemplateInstanceDao;
	
	public EditDNFilterTemplateInstancePage(final Integer ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rename template instances > Edit", 
			"Edit a DN rename template instance"
		);
		
		// prepare DAO objects
		//
		this.dnFilterTemplateInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNFilterTemplateInstanceDao.class);
		
		// register page components
		//
		addHelpWindow(new DNFilterTemplateInstanceHelpPanel("content"));
		
		DNFilterTemplateInstance instance = dnFilterTemplateInstanceDao.load(ruleId);
		
		add(
			new RedirectWithParamButton(
				DNGroupDetailPage.class,
				instance.getGroupId(), 
				"manageGroupRules"
			)
		);
		
		addEditDNFilterTemplateInstanceForm(instance);
	}

	private void addEditDNFilterTemplateInstanceForm(final DNFilterTemplateInstance instance)
	{
		IModel<DNFilterTemplateInstance> formModel = new CompoundPropertyModel<DNFilterTemplateInstance>(instance);
		
		Form<DNFilterTemplateInstance> form = new Form<DNFilterTemplateInstance>("editDNFilterTemplateInstanceForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNFilterTemplateInstance instance = this.getModelObject();
				
				try {
					dnFilterTemplateInstanceDao.update(instance);
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
						"The filter template instance could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The filter template instance was successfuly updated.");
				setResponsePage(new DNGroupDetailPage(instance.getGroupId()));
			}
		};
		
		form.add(createTextfield("propertyName"));
		form.add(createTextfield("pattern"));
		form.add(createCheckbox("keep"));
		
		add(form);
	}
}
