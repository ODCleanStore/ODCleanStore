package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.PublisherDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewRestrictionPage extends FrontendPage
{
	private Dao<QARule> qaRuleDao;
	private Dao<Publisher> publisherDao;

	private Publisher restriction;
	
	public NewRestrictionPage(final Long ruleId) 
	{
		super(
			"Home > Transformers > QA > Rules management > Restrictions > Create", 
			"Add a new publisher restriction"
		);
		
		// prepare DAO objects
		//
		qaRuleDao = daoLookupFactory.getDao(QARuleDao.class);
		publisherDao = daoLookupFactory.getDao(PublisherDao.class);
		
		// register page components
		//
		final QARule rule = qaRuleDao.load(ruleId);
		
		addGoBackLink(rule.getId());
		addNewRestrictionForm(rule);
	}
	
	private void addGoBackLink(final Long ruleId)
	{
		add(new Link("manageRuleRestrictions")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() 
			{
				setResponsePage(
					new ManageQARuleRestrictionsPage(ruleId)
				);
			}
		});
	}
	
	private void addNewRestrictionForm(final QARule rule)
	{
		this.restriction = new Publisher();
		IModel formModel = new CompoundPropertyModel(this);
		
		Form<Publisher> form = new Form<Publisher>("newRestrictionForm", formModel)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{
				Publisher restriction = NewRestrictionPage.this.restriction;
						
				rule.addPublisherRestriction(restriction);
				
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
					// logger.error(ex.getMessage());
					
					getSession().error(
						"The restriction could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The restriction was successfuly registered.");
				setResponsePage(new ManageQARuleRestrictionsPage(rule.getId()));
			}
		};
		
		form.add(createEnumSelectbox(publisherDao, "restriction"));
		
		add(form);
	}
}
