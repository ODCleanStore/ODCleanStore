package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QARuleHelpPanel;

@AuthorizeInstantiation({ "PIC" })
public class OIGroupDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(OIGroupDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<OIRulesGroup> oiRulesGroupDao;
	private DaoForEntityWithSurrogateKey<OIRule> oiRuleDao;
	
	public OIGroupDetailPage(final Integer groupId) 
	{
		super(
			"Home > Backend > OI > Groups > Detail", 
			"Show OI rule group detail"
		);
		
		// prepare DAO objects
		//
		oiRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRulesGroupDao.class);
		oiRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRuleDao.class);
		
		// register page components
		//		
		addHelpWindow("rulesGroupHelpWindow", "openRulesGroupHelpWindow", new RulesGroupHelpPanel("content"));
		addHelpWindow("oiRuleHelpWindow", "openOIRuleHelpWindow", new OIRuleHelpPanel("content"));
		addGroupInformationSection(groupId);
		addOIRulesSection(groupId);
	}
	
	private void addGroupInformationSection(final Integer groupId)
	{
		setDefaultModel(createModelForOverview(oiRulesGroupDao, groupId));
		
		add(new Label("label"));
		add(new Label("description"));
	}
	
	private void addOIRulesSection(final Integer groupId) 
	{
		add(
			new RedirectWithParamButton(
				NewOIRulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addOIRulesTable(groupId);
	}

	private void addOIRulesTable(final Integer groupId) 
	{
		SortableDataProvider<OIRule> data = new DependentSortableDataProvider<OIRule>
		(
			oiRuleDao, 
			"label", 
			"groupId", 
			groupId
		);
		
		DataView<OIRule> dataView = new DataView<OIRule>("oiRulesTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<OIRule> item) 
			{
				OIRule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<OIRule>(rule));
				
				item.add(new Label("label"));
				item.add(new Label("linkType"));
				item.add(createNullResistentTableCellLabel("sourceRestriction", rule.getSourceRestriction()));
				item.add(createNullResistentTableCellLabel("targetRestriction", rule.getTargetRestriction()));
				item.add(new TruncatedLabel("linkageRule", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(createNullResistentTableCellLabel("filterThreshold", rule.getFilterThreshold()));
				item.add(createNullResistentTableCellLabel("filterLimit", rule.getFilterLimit()));
				
				item.add(
					new DeleteRawButton<OIRule>
					(
						oiRuleDao,
						rule.getId(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						OIGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						OIRuleDetailPage.class,
						rule.getId(),
						"showOIRuleDetail"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditOIRulePage.class,
						rule.getId(),
						"showEditOIRulePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<OIRule>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<OIRule>("sortByLinkType", "linkType", data, dataView));
		add(new SortTableButton<OIRule>("sortBySourceRestriction", "sourceRestriction", data, dataView));
		add(new SortTableButton<OIRule>("sortByTargetRestriction", "targetRestriction", data, dataView));
		add(new SortTableButton<OIRule>("sortByFilterThreshold", "filterThreshold", data, dataView));
		add(new SortTableButton<OIRule>("sortByFilterLimit", "filterLimit", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
}
