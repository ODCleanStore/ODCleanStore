package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import java.util.List;

import org.apache.log4j.Logger;
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

public class QARulesManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QARulesManagementPage.class);
	
	private Dao<QARule> qaRuleDao;
	private Dao<Publisher> publisherDao;

	public QARulesManagementPage() 
	{
		super(
			"Home > Transformers > QA > Rules management", 
			"QA Rules management"
		);
		
		// prepare DAO objects
		//
		qaRuleDao = daoLookupFactory.getDao(QARuleDao.class);
		publisherDao = daoLookupFactory.getDao(PublisherDao.class);
		
		// register page components
		//
		addQARulesTable();
		addPublishersTable();
	}

	/*
	 	=======================================================================
	 	Implementace publishersTable
	 	=======================================================================
	*/
	
	private void addPublishersTable()
	{
		List<Publisher> allPublihers = publisherDao.loadAll();
		
		ListView<Publisher> listView = new ListView<Publisher>("publishersTable", allPublihers)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<Publisher> item) 
			{
				final Publisher publisher = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Publisher>(publisher));

				item.add(new Label("label"));	
				item.add(new Label("uri"));	
				
				addDeleteButton(item, publisher);
			}
		};
		
		add(listView);
	}
	
	private void addDeleteButton(ListItem<Publisher> item, final Publisher publisher)
	{
		Link button = new Link("deletePublisher")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
            	publisherDao.delete(publisher);
            	
				getSession().info("The publisher was successfuly deleted.");
				setResponsePage(QARulesManagementPage.class);
            }
        };
        
        button.add(new ConfirmationBoxRenderer(
        	"Are you sure you want to delete the publisher and all associated restrictions?"
        ));
        
		item.add(button);
	}
	
	/*
	 	=======================================================================
	 	Implementace qaRulesTable
	 	=======================================================================
	*/
	
	private void addQARulesTable()
	{
		List<QARule> allRules = qaRuleDao.loadAll();
		
		ListView<QARule> listView = new ListView<QARule>("qaRulesTable", allRules)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<QARule> item) 
			{
				final QARule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<QARule>(rule));
				
				item.add(new Label("filter"));
				item.add(new Label("coefficient"));
				item.add(new Label("description"));	
				
				addDeleteButton(item, rule);
				addManageRuleRestrictionsButton(item, rule);
			}
		};
		
		add(listView);
	}
	
	private void addDeleteButton(ListItem<QARule> item, final QARule rule)
	{
		Link button = new Link("deleteRule")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
            	qaRuleDao.delete(rule);
            	
				getSession().info("The rule was successfuly deleted.");
				setResponsePage(QARulesManagementPage.class);
            }
        };
        
        button.add(new ConfirmationBoxRenderer(
        	"Are you sure you want to delete the rule and all associated restrictions?"
        ));
        
		item.add(button);
	}
	
	private void addManageRuleRestrictionsButton(ListItem<QARule> item, final QARule rule)
	{
		Link button = new Link("manageRuleRestrictions")
        {
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick()
            {
            	setResponsePage(
					new ManageQARuleRestrictionsPage(rule.getId())
				);
            }
        };
        
		item.add(button);
	}
}
