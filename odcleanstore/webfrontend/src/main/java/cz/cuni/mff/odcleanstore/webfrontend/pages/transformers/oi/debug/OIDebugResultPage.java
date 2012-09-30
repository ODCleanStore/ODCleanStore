package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.debug;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import cz.cuni.mff.odcleanstore.linker.impl.DebugResult;
import cz.cuni.mff.odcleanstore.linker.impl.LinkedPair;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class OIDebugResultPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	public OIDebugResultPage(List<DebugResult> results, Integer ruleGroupId) 
	{
		super(
			"Home > Backend > OI > Groups > Debug results", 
			"Results of OI rule group debugging"
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
				ListView<LinkedPair> rows = new ListView<LinkedPair>(
						"resultRow", result.getLinks())
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<LinkedPair> item) {
						LinkedPair pair = item.getModelObject();
						String sourceEntity = pair.getFirstLabel();
						if (sourceEntity == null)
						{
							sourceEntity = pair.getFirstUri();
						}
						item.add(new Label("sourceEntity", sourceEntity));
						
						String targetEntity = pair.getSecondLabel();
						if (targetEntity == null)
						{
							targetEntity = pair.getSecondUri();
						}
						item.add(new Label("targetEntity", targetEntity));
						
						item.add(new Label("confidence", pair.getConfidence().toString()));
					}
					
				};
				item.add(new Label("ruleLabel", result.getRule().getLabel()));
				item.add(rows);
			}		
		};
		
		add(tables);
	}
}
