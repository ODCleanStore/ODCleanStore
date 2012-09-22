package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ "POC" })
public class QAGroupDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QAGroupDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<QARulesGroup> qaRulesGroupDao;
	private DaoForEntityWithSurrogateKey<QARule> qaRuleDao;

	public QAGroupDetailPage(final Long groupId) 
	{
		super(
			"Home > Backend > QA > Groups > Detail", 
			"Show QA rule group detail"
		);
		
		// prepare DAO objects
		//
		qaRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(QARulesGroupDao.class);
		qaRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(QARuleDao.class);
		
		// register page components
		//
		addHelpWindow("rulesGroupHelpWindow", "openRulesGroupHelpWindow", new RulesGroupHelpPanel("content"));
		addHelpWindow("qaRuleHelpWindow", "openQARuleHelpWindow", new QARuleHelpPanel("content"));
		addGroupInformationSection(groupId);
		addQARulesSection(groupId);
	}

	private void addGroupInformationSection(final Long groupId)
	{
		setDefaultModel(createModelForOverview(qaRulesGroupDao, groupId));
		
		add(new Label("label"));
		add(new Label("description"));
	}
	
	private void addQARulesSection(final Long groupId) 
	{
		add(
			new RedirectWithParamButton(
				NewQARulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addQARulesTable(groupId);
	}
	
	private void addQARulesTable(final Long groupId)
	{
		SortableDataProvider<QARule> data = new DependentSortableDataProvider<QARule>
		(
			qaRuleDao, 
			"coefficient", 
			"groupId", 
			groupId
		);
		
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
					new DeleteRawButton<QARule>
					(
						qaRuleDao,
						rule.getId(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						QAGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						QARuleDetailPage.class, 
						rule.getId(), 
						"showQARuleDetailPage"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditQARulePage.class,
						rule.getId(),
						"showEditQARulePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<QARule>("sortByCoefficient", "coefficient", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}
