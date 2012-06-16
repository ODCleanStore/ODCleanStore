package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

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
		IModel<List<Publisher>> model = createModelForListView(publisherDao);
		
		ListView<Publisher> listView = new ListView<Publisher>("publishersTable", model)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<Publisher> item) 
			{
				final Publisher publisher = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Publisher>(publisher));

				item.add(new Label("label"));	
				item.add(new Label("uri"));	
				
				item.add(
					createDeleteButton(
						publisherDao, 
						publisher, 
						"deletePublisher", 
						"publisher", 
						"restriction", 
						QARulesManagementPage.class
					)
				);
			}
		};
		
		add(listView);
	}
	
	/*
	 	=======================================================================
	 	Implementace qaRulesTable
	 	=======================================================================
	*/
	
	private void addQARulesTable()
	{
		IModel<List<QARule>> model = createModelForListView(qaRuleDao);
				
		ListView<QARule> listView = new ListView<QARule>("qaRulesTable", model)
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
				
				item.add(
					createDeleteButton(
						qaRuleDao, 
						rule, 
						"deleteRule", 
						"rule", 
						"restriction", 
						QARulesManagementPage.class
					)
				);

				item.add(
					createGoToPageButton(
						ManageQARuleRestrictionsPage.class,
						rule.getId(), 
						"manageRuleRestrictions"
					)
				);
			}
		};
		
		add(listView);
	}
}
