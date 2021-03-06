package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.QARuleValidator;

/**
 * Add-new-QA-rule page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewQARulePage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewQARulePage.class);
	
	private QARuleDao qaRuleDao;
	
	/**
	 * 
	 * @param groupId
	 */
	public NewQARulePage(final Integer groupId) 
	{
		super(
			"Home > Backend > Quality Assessment > Groups > Rules > New", 
			"Add a new Quality Assessment rule",
			QARulesGroupDao.class,
			groupId
		);
		
		// prepare DAO objects
		//
		this.qaRuleDao = daoLookupFactory.getDao(QARuleDao.class, isEditable());
		
		// register page components
		//
		addHelpWindow(new QARuleHelpPanel("content"));
		
		add(
			new RedirectWithParamButton(
				QAGroupDetailPage.class,
				groupId, 
				"manageGroupRules"
			)
		);
		
		addNewQARuleForm(groupId);
	}

	/**
	 * 
	 * @param groupId
	 */
	private void addNewQARuleForm(final Integer groupId)
	{
		IModel<QARule> formModel = new CompoundPropertyModel<QARule>(new QARule());
		
		Form<QARule> form = new Form<QARule>("newQARuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				QARule rule = this.getModelObject();
				rule.setGroupId(groupId);
				
				try {
					qaRuleDao.save(rule);
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
						"The rule could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly registered.");
				setResponsePage(new QAGroupDetailPage(groupId));
			}
		};
		
		form.add(createTextfield("label"));

		TextArea<String> filter = new TextArea<String>("filter");
		filter.setRequired(true);
		filter.add(new QARuleValidator(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials()));
		form.add(filter);

		addCoefficientTextfield(form);
		form.add(createTextarea("description", false));
		
		add(form);
	}

	/**
	 * 
	 * @param form
	 */
	private void addCoefficientTextfield(Form<QARule> form)
	{
		TextField<String> textfield = new TextField<String>("coefficient");
		
		textfield.setRequired(true);
		textfield.add(new RangeValidator<Double>(0.0, 1.0));
		form.add(textfield);
	}
}
