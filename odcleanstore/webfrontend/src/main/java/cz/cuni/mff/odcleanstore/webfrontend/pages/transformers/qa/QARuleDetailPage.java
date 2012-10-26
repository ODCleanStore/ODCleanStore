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
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.QARuleValidator;

@AuthorizeInstantiation({ Role.PIC })
public class QARuleDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(QARuleDetailPage.class);
	
	private QARuleDao qaRuleDao;
	
	public QARuleDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > Quality Assessment > Groups > Rules > Edt", 
			"Edit a Quality Assessment rule",
			QARuleDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		this.qaRuleDao = daoLookupFactory.getDao(QARuleDao.class, isEditable());
		
		// register page components
		//
		addHelpWindow(new QARuleHelpPanel("content"));
		
		QARule rule = qaRuleDao.load(ruleId);
		
		add(
			new RedirectWithParamButton(
				QAGroupDetailPage.class,
				rule.getGroupId(), 
				"manageGroupRules"
			)
		);
		
		addEditQARuleForm(rule);
	}

	private void addEditQARuleForm(final QARule rule)
	{
		IModel<QARule> formModel = new CompoundPropertyModel<QARule>(rule);
		
		Form<QARule> form = new LimitedEditingForm<QARule>("editQARuleForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				QARule rule = this.getModelObject();
				
				try {
					qaRuleDao.update(rule);
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
						"The rule could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly updated.");
				setResponsePage(new QAGroupDetailPage(rule.getGroupId()));
			}
		};
		
		TextArea<String> filter = new TextArea<String>("filter");
		filter.setRequired(true);
		filter.add(new QARuleValidator(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials()));
		form.add(filter);

		addCoefficientTextfield(form);
		form.add(createTextarea("description", false));
		
		add(form);
	}

	private void addCoefficientTextfield(Form<QARule> form)
	{
		TextField<String> textfield = new TextField<String>("coefficient");
		
		textfield.setRequired(true);
		textfield.add(new RangeValidator<Double>(0.0, 1.0));
		form.add(textfield);
	}
}
