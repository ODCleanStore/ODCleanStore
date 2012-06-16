package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.Publisher;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.PublisherDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class ManageQARuleRestrictionsPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private Dao<QARule> qaRuleDao;
	private Dao<Publisher> publisherDao;

	public ManageQARuleRestrictionsPage(final Long ruleId) 
	{
		super(
			"Home > Transformers > QA > Rules management > Restrictions", 
			"Manage QA rule restrictions"
		);
		
		// prepare DAO objects
		//
		qaRuleDao = daoLookupFactory.getDao(QARuleDao.class);
		publisherDao = daoLookupFactory.getDao(PublisherDao.class);
		
		// register page components
		//
		final QARule rule = qaRuleDao.load(ruleId);
		
		addRuleInformationSection(ruleId);
		addRestrictionsSection(rule);
	}

	/*
	 	=======================================================================
	 	Rule information section
	 	=======================================================================
	*/

	private void addRuleInformationSection(final Long ruleId)
	{
		setDefaultModel(createModelForOverview(qaRuleDao, ruleId));
		
		add(new Label("description"));
		add(new Label("filter"));
		add(new Label("coefficient"));
	}
	
	/*
	 	=======================================================================
	 	Implementace restrictionsTable
	 	=======================================================================
	*/
	
	private void addRestrictionsSection(QARule rule) 
	{
		add(
			createGoToPageButton(
				NewRestrictionPage.class,
				rule.getId(), 
				"addNewRestrictionLink"
			)
		);
		
		addRestrictionsTable(rule);
	}

	private void addRestrictionsTable(final QARule rule) 
	{
		List<Publisher> allRestrictions = new LinkedList<Publisher>(rule.getPublisherRestrictions());
		
		ListView<Publisher> listView = new ListView<Publisher>("restrictionsTable", allRestrictions)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<Publisher> item) 
			{
				final Publisher publisher = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Publisher>(publisher));
				
				item.add(new Label("label"));
				item.add(new Label("uri"));	
				
				addDeleteButton(item, rule, publisher);
			}
		};
		
		add(listView);
	}
	
	private void addDeleteButton(ListItem<Publisher> item, final QARule rule, final Publisher publisher)
	{
		Link button = new Link("deleteRestriction")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
				rule.removePublisherRestriction(publisher);
				
				try {
					qaRuleDao.update(rule);
				}
				catch (Exception ex)
				{
					getSession().error(
						"The restriction could not be deleted due to an unexpected error."
					);
					
					return;
				}
            	
				getSession().info("The restriction was successfuly deleted.");
				setResponsePage(new ManageQARuleRestrictionsPage(rule.getId()));
            }
        };
        
        button.add(new ConfirmationBoxRenderer("Are you sure you want to delete the restriction?"));
        
		item.add(button);
	}
}
