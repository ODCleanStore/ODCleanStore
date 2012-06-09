package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
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
		addOIRulesSection(group);
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
	
	private void addOIRulesSection(OIRulesGroup group) 
	{
		addNewRestrictionLink(group.getId());
		addOIRulesTable(group);
	}
	
	private void addNewRestrictionLink(final Long ruleId)
	{
		add(new Link("addNewRuleLink")
		{
			private static final long serialVersionUID = 1L;
	
			@Override
			public void onClick() 
			{
				setResponsePage(
					new NewOIRulePage(ruleId)
				);
			}
		});
	}
	
	private void addOIRulesTable(final OIRulesGroup group) 
	{
		List<OIRule> allRules = new LinkedList<OIRule>(group.getRules());
		
		ListView<OIRule> listView = new ListView<OIRule>("oiRulesTable", allRules)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<OIRule> item) 
			{
				final OIRule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<OIRule>(rule));
				
				item.add(new Label("definition"));
				
				addDeleteButton(item, group.getId(), rule.getId());
			}
		};
		
		add(listView);
	}
	
	private void addDeleteButton(ListItem<OIRule> item, final Long groupId, final Long ruleId)
	{
		Link button = new Link("deleteRule")
	    {
			private static final long serialVersionUID = 1L;
	
			@Override
	        public void onClick()
	        {
				oiRuleDao.deleteRaw(ruleId);
	        	
				getSession().info("The rule was successfuly deleted.");
				setResponsePage(new ManageGroupRulesPage(groupId));
	        }
	    };
	    
	    button.add(new ConfirmationBoxRenderer("Are you sure you want to delete the rule?"));
	    
		item.add(button);
	}
}
