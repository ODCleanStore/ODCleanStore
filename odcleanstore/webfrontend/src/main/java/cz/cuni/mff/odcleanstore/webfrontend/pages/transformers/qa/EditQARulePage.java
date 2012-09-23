package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class EditQARulePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<QARule> qaRuleDao;
	
	public EditQARulePage(final Long ruleId) 
	{
		super(
			"Home > Backend > QA > Groups > Rules > Edt", 
			"Edit a QA rule"
		);
		
		// prepare DAO objects
		//
		this.qaRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(QARuleDao.class);
		
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
		
		Form<QARule> form = new Form<QARule>("editQARuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				QARule rule = this.getModelObject();
				
				try {
					qaRuleDao.update(rule);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
					getSession().error(
						"The rule could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly updated.");
				setResponsePage(new QAGroupDetailPage(rule.getGroupId()));
			}
		};
		
		form.add(createTextarea("filter"));
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
