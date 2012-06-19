package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class QARulesManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QARulesManagementPage.class);
	
	private Dao<QARulesGroup> qaRulesGroupDao;
	private Dao<QARule> qaRuleDao;

	public QARulesManagementPage(final Long groupId) 
	{
		super(
			"Home > Transformers > QA > Rules management", 
			"QA Rules management"
		);
		
		// prepare DAO objects
		//
		qaRulesGroupDao = daoLookupFactory.getDao(QARulesGroupDao.class);
		qaRuleDao = daoLookupFactory.getDao(QARuleDao.class);
		
		// register page components
		//
		addGroupInformationSection(groupId);
		addQARulesSection(groupId);
	}
	
	/*
	 	=======================================================================
	 	Implementace qaRulesTable
	 	=======================================================================
	*/
	
	private void addGroupInformationSection(final Long groupId)
	{
		setDefaultModel(createModelForOverview(qaRulesGroupDao, groupId));
		
		add(new Label("label"));
		add(new Label("description"));
	}
	
	private void addQARulesSection(final Long groupId) 
	{
		add(
			createGoToPageButton(
				NewQARulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addQARulesTable(groupId);
	}
	
	private void addQARulesTable(final Long groupId)
	{
		IDataProvider<QARule> data = new QARuleDataProvider(qaRuleDao, groupId);
		
		DataView<QARule> dataView = new DataView<QARule>("qaRulesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<QARule> item) 
			{
				QARule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<QARule>(rule));
				
				item.add(new Label("filter"));
				item.add(new Label("coefficient"));
				item.add(new Label("description"));	
				
				item.add(
					createDeleteRawButton(
						qaRuleDao, 
						rule.getId(), 
						"deleteRule", 
						"rule",
						QARulesManagementPage.this
					)
				);
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
