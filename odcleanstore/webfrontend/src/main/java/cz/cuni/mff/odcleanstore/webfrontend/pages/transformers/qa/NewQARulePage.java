package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewQARulePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private Dao<QARule> qaRuleDao;
	
	public NewQARulePage() 
	{
		super(
			"Home > Transformers > QA > Rules management > Create", 
			"Add new QA rule"
		);
		
		// prepare DAO objects
		//
		this.qaRuleDao = daoLookupFactory.getDao(QARuleDao.class);
		
		// register page components
		//
		addNewQARuleForm();
	}

	private void addNewQARuleForm()
	{
		IModel<QARule> formModel = new CompoundPropertyModel<QARule>(new QARule());
		
		Form<QARule> newQARuleForm = new Form<QARule>("newQARuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				QARule rule = this.getModelObject();
				
				qaRuleDao.save(rule);
				
				getSession().info("The rule was successfuly registered.");
				setResponsePage(QARulesManagement.class);
			}
		};
		
		addDescriptionTextarea(newQARuleForm);
		addFilterTextarea(newQARuleForm);
		addCoefficientTextfield(newQARuleForm);
		
		add(newQARuleForm);
	}
	
	private void addDescriptionTextarea(Form<QARule> form)
	{
		TextArea<String> textarea = new TextArea<String>("description");
		textarea.setRequired(true);
		form.add(textarea);
	}
	
	private void addFilterTextarea(Form<QARule> form)
	{
		TextArea<String> textarea = new TextArea<String>("filter");
		textarea.setRequired(true);
		form.add(textarea);
	}
	
	private void addCoefficientTextfield(Form<QARule> form)
	{
		TextField<String> textfield = new TextField<String>("coefficient");
		textfield.setRequired(true);
		form.add(textfield);
	}
}
