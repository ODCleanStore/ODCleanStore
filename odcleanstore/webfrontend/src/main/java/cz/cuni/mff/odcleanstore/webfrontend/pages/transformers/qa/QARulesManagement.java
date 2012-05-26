package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class QARulesManagement extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QARulesManagement.class);
	
	private QARuleDao qaRuleDao;

	public QARulesManagement() 
	{
		super(
			"Home > Transformers > QA > Rules management", 
			"QA Rules management"
		);
		
		// prepare DAO objects
		//
		qaRuleDao = daoLookupFactory.getQARuleDao();
		
		// register page components
		//
		addQARulesTable();
	}

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
				
				item.add(new Label("description"));
				item.add(new Label("filter"));
				item.add(new Label("coefficient"));	
				
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
				setResponsePage(QARulesManagement.class);
            }
        };
        
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
            	/*
            	setResponsePage(
					new EditAccountPermissionsPage(user.getId())
				);
				*/
            }
        };
        
		item.add(button);
	}
}
