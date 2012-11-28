package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.debug;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;

import cz.cuni.mff.odcleanstore.linker.impl.DebugResult;
import cz.cuni.mff.odcleanstore.linker.impl.LinkedPair;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * Policy creator can see a results of DN debugging on this page.
 * Two tables are displayed for each rule. One for created links between input data and data in clean DB,
 * another one for links within input data. 
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.PIC })
public class OIDebugResultPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	public OIDebugResultPage(List<DebugResult> results, Integer ruleGroupId) 
	{
		super(
			"Home > Backend > Linker > Groups > Debug results", 
			"Results of Linker rule group debugging"
		);
			
		// register page components
		//
		add(new RedirectWithParamButton(
			OIDebugPage.class,
			ruleGroupId, 
			"backToInputLink"
		));
		addResultTables(results);
	}

	private void addResultTables(List<DebugResult> results) 
	{
		ListView<DebugResult> tables = new ListView<DebugResult>("resultTable", results)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<DebugResult> item) {
				DebugResult result = item.getModelObject();
				PageableListView<LinkedPair> rows = new PageableListView<LinkedPair>(
						"resultRow", result.getLinks(), ITEMS_PER_PAGE)
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<LinkedPair> item) {
						LinkedPair pair = item.getModelObject();
						
						Label label = new Label("sourceURI", pair.getFirstUri());
						label.add(new AttributeModifier("title", pair.getFirstLabel()));
						item.add(label);
						
						label = new Label("targetURI", pair.getSecondUri());
						label.add(new AttributeModifier("title", pair.getSecondLabel()));
						item.add(label);
						
						item.add(new Label("confidence", pair.getConfidence().toString()));
					}
				};
				
				item.add(new Label("ruleLabel", result.getRuleLabel()));
				item.add(rows);
				item.add(new UnobtrusivePagingNavigator("navigator", rows));
			}		
		};
		tables.setReuseItems(true);
		add(tables);
	}
}
