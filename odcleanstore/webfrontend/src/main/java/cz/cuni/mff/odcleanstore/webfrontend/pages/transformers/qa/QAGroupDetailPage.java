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
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class QAGroupDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QAGroupDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<QARulesGroup> qaRulesGroupDao;
	private DaoForEntityWithSurrogateKey<QARule> qaRuleDao;

	public QAGroupDetailPage(final Long groupId) 
	{
		super(
			"Home > QA > Rules groups > Group > Detail", 
			"QA Rules management"
		);
		
		// prepare DAO objects
		//
		qaRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(QARulesGroupDao.class);
		qaRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(QARuleDao.class);
		
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
			new RedirectButton(
				NewQARulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addQARulesTable(groupId);
	}
	
	private void addQARulesTable(final Long groupId)
	{
		IDataProvider<QARule> data = new DependentDataProvider<QARule>(qaRuleDao, "groupId", groupId);
		
		DataView<QARule> dataView = new DataView<QARule>("qaRulesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<QARule> item) 
			{
				QARule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<QARule>(rule));
				
				item.add(new TruncatedLabel("filter", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new Label("coefficient"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteButton<QARule>
					(
						qaRuleDao,
						rule.getId(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						QAGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectButton
					(
						QARuleDetailPage.class, 
						rule.getId(), 
						"showQARuleDetailPage"
					)
				);
				
				item.add(
					new RedirectButton
					(
						EditQARulePage.class,
						rule.getId(),
						"showEditQARulePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}