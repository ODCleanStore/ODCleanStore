package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class ManageGroupRulesPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<OIRulesGroup> oiRulesGroupDao;
	private Dao<OIRule> oiRuleDao;
	
	public ManageGroupRulesPage(final Long groupId) 
	{
		super(
			"Home > Transformers > OI > Rules management > Rules groups > Rules", 
			"Manage OI rules"
		);
		
		// prepare DAO objects
		//
		oiRulesGroupDao = daoLookupFactory.getDao(OIRulesGroupDao.class);
		oiRuleDao = daoLookupFactory.getDao(OIRuleDao.class);
		
		// register page components
		//
		OIRulesGroup group = oiRulesGroupDao.load(groupId);
		
		addGroupInformationSection(group);
		addOIRulesSection(group.getId());
	}

	/*
	 	=======================================================================
	 	Group information section
	 	=======================================================================
	*/

	private void addGroupInformationSection(final OIRulesGroup group)
	{
		setDefaultModel(new CompoundPropertyModel<OIRulesGroup>(group));
		
		add(new Label("label"));
		add(new Label("description"));
	}
	
	/*
 		=======================================================================
	 	Implementace oiRulesTable
	 	=======================================================================
	*/
	
	private void addOIRulesSection(Long groupId) 
	{
		add(
			createGoToPageButton(
				NewOIRulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addOIRulesTable(groupId);
	}

	private void addOIRulesTable(final Long groupId) 
	{
		IModel<List<OIRule>> model = new LoadableDetachableModel<List<OIRule>>() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected List<OIRule> load() 
			{
				return oiRulesGroupDao.load(groupId).getRules();
			}
		};
				
		ListView<OIRule> listView = new ListView<OIRule>("oiRulesTable", model)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<OIRule> item) 
			{
				final OIRule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<OIRule>(rule));
				
				item.add(new Label("definition"));
				
				item.add(
					createDeleteRawButton(
						oiRuleDao, 
						rule.getId(), 
						"deleteRule", 
						"rule", 
						ManageGroupRulesPage.this
					)
				);
			}
		};
		
		add(listView);
	}
	
}
