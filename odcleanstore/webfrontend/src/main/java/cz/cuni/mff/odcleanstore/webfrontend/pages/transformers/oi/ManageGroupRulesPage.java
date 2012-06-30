package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class ManageGroupRulesPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(ManageGroupRulesPage.class);
	
	private DaoForEntityWithSurrogateKey<OIRulesGroup> oiRulesGroupDao;
	private DaoForEntityWithSurrogateKey<OIRule> oiRuleDao;
	
	public ManageGroupRulesPage(final Long groupId) 
	{
		super(
			"Home > Transformers > OI > Rules management > Rules groups > Rules", 
			"Manage OI rules"
		);
		
		// prepare DAO objects
		//
		oiRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRulesGroupDao.class);
		oiRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRuleDao.class);
		
		// register page components
		//		
		addGroupInformationSection(groupId);
		addOIRulesSection(groupId);
	}

	/*
	 	=======================================================================
	 	Group information section
	 	=======================================================================
	*/

	private void addGroupInformationSection(final Long groupId)
	{
		setDefaultModel(createModelForOverview(oiRulesGroupDao, groupId));
		
		add(new Label("label"));
		add(new Label("description"));
	}
	
	/*
 		=======================================================================
	 	Implementace oiRulesTable
	 	=======================================================================
	*/
	
	private void addOIRulesSection(final Long groupId) 
	{
		add(
			new RedirectButton(
				NewOIRulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addOIRulesTable(groupId);
	}

	private void addOIRulesTable(final Long groupId) 
	{		
		IDataProvider<OIRule> data = new OIRuleDataProvider(oiRuleDao, groupId);
		
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
				item.add(new Label("linkageRule"));
				item.add(createNullResistentTableCellLabel("filterThreshold", rule.getFilterThreshold()));
				item.add(createNullResistentTableCellLabel("filterLimit", rule.getFilterLimit()));
				
				item.add(
					new DeleteButton<OIRule>
					(
						oiRuleDao,
						rule.getId(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						ManageGroupRulesPage.this
					)
				);
				
				item.add(
					new RedirectButton
					(
						OIRuleDetailPage.class,
						rule.getId(),
						"showOIRuleDetail"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
}
