package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewOIRulePage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	// private Dao<OIRulesGroup> oiRulesGroupDao;
	private Dao<OIRule> oiRuleDao;
	
	public NewOIRulePage(final Long groupId) 
	{
		super(
			"Home > Transformers > OI > Rules management > Rules groups > Rules > Create", 
			"Add a new OI rule"
		);
		
		// prepare DAO objects
		//
		// oiRulesGroupDao = daoLookupFactory.getDao(OIRulesGroupDao.class);
		oiRuleDao = daoLookupFactory.getDao(OIRuleDao.class);
		
		// register page components
		//
		addGoBackLink(groupId);
		addNewOIRuleForm(groupId);
	}

	private void addGoBackLink(final Long groupId)
	{
		add(new Link("manageGroupRules")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() 
			{
				setResponsePage(
					new ManageGroupRulesPage(groupId)
				);
			}
		});
	}
	
	private void addNewOIRuleForm(final Long groupId)
	{
		IModel formModel = new CompoundPropertyModel(new OIRule());
		
		Form<OIRule> newOIRuleForm = new Form<OIRule>("newOIRuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{
				OIRule rule = getModelObject();
				rule.setGroupId(groupId);
				
				try {
					oiRuleDao.save(rule);
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
						"The rule could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly registered.");
				setResponsePage(new ManageGroupRulesPage(groupId));
			}
		};
		
		addDefinitionTextarea(newOIRuleForm);
		
		add(newOIRuleForm);
	}
	
	private void addDefinitionTextarea(Form<OIRule> form)
	{
		TextArea<String> textarea = new TextArea<String>("definition");
		textarea.setRequired(true);
		form.add(textarea);
	}
}
