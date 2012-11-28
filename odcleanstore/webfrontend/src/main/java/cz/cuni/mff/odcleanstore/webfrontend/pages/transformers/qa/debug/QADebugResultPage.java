package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.debug;

import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RoundedNumberLabel;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QARuleDetailPage;

/**
 * Policy creator can see a results of QA debugging on this page.
 * One table is displayed for each RDF graph in input data.
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.PIC })
public class QADebugResultPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	public QADebugResultPage(List<GraphScoreWithTrace> results, Integer ruleGroupId) 
	{
		super(
			"Home > Backend > Quality Assessment > Groups > Debug results", 
			"Results of Quality Assessment rule group debugging"
		);
			
		// register page components
		//
		add(new RedirectWithParamButton(
			QADebugPage.class,
			ruleGroupId, 
			"backToInputLink"
		));
		addResultTables(results);
	}

	private void addResultTables(List<GraphScoreWithTrace> results) 
	{	
		ListView<GraphScoreWithTrace> tables = new ListView<GraphScoreWithTrace>("resultTable", results)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<GraphScoreWithTrace> item) {
				GraphScoreWithTrace result = item.getModelObject();
				ListView<QualityAssessmentRule> rows = new ListView<QualityAssessmentRule>(
						"resultRow", result.getTrace())
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<QualityAssessmentRule> item) {
						QualityAssessmentRule rule = item.getModelObject();
						
						item.add(new Label("description", rule.getDescription()));
						
						item.add(new RoundedNumberLabel("coefficient", rule.getCoefficient()));
						
						item.add(
							new RedirectWithParamButton
							(
								QARuleDetailPage.class,
								rule.getId(),
								"showQARuleDetailPage"
							)
						);			
					}
					
				};
				item.add(new Label("graphLabel", result.getGraphName() + ", Total score: "));
				item.add(new RoundedNumberLabel("graphScore", result.getScore()));
				item.add(rows);
			}		
		};
		
		add(tables);
	}
}
