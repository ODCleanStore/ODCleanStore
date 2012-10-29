package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.debug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl.GraphModification;
import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl.RuleModification;
import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl.TripleModification;
import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButtonWithLabel;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.DNRuleDetailPage;

@AuthorizeInstantiation({ Role.PIC })
public class DNDebugResultPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	public DNDebugResultPage(List<GraphModification> results, Integer ruleGroupId) 
	{
		super(
			"Home > Backend > Data Normalization > Groups > Debug results", 
			"Results of Data Normalization rule group debugging"
		);
			
		// register page components
		//
		add(new RedirectWithParamButton(
			DNDebugPage.class,
			ruleGroupId, 
			"backToInputLink"
		));
		addResultTables(results);
	}

	private void addResultTables(List<GraphModification> results) 
	{	
		ListView<GraphModification> tables = new ListView<GraphModification>("resultTable", results)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<GraphModification> item) {
				GraphModification result = item.getModelObject();
				List<ModificationRecord> modifications = flatten(result);
				
				Collections.sort(modifications, new Comparator<ModificationRecord>() {

					public int compare(ModificationRecord o1, ModificationRecord o2) {

						int r = o1.getRule().getId().compareTo(o2.getRule().getId());
						int s = o1.getSubject().compareTo(o2.getSubject());
						int p = o1.getPredicate().compareTo(o2.getPredicate());
						int o = o1.getObject().compareTo(o2.getObject());
						int t = o1.getType().order - o2.getType().order;
						
						if (r != 0) return r; //Group by rule
						if (s != 0) return s; //Order by s p t o
						if (p != 0) return p;
						if (t != 0) return t; //Delete first, Insert second
						if (o != 0) return o;
						
						return 0;
					}
					
				});
				
				ListView<ModificationRecord> rows = new ListView<ModificationRecord>("resultRow", modifications)
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(ListItem<ModificationRecord> item) {
						ModificationRecord record = item.getModelObject();
						
						item.add(new AttributeModifier("class", new Model<String>("dn" + record.getType().toString())));
						
						item.add(new Label("modification", record.getType().toString()));
						
						item.add(new Label("subject", record.getSubject()));						
						item.add(new Label("predicate", record.getPredicate()));
						item.add(new Label("object", record.getObject()));
						
						item.add(
							new RedirectWithParamButtonWithLabel
							(
								DNRuleDetailPage.class,
								"showDNRuleDetailPage",
								record.getRule().getDescription(),
								record.getRule().getId()
							)
						);			
					}
					
				};
				item.add(new Label("graphName", result.getGraphName()));
				item.add(rows);
			}		
		};
		
		add(tables);
	}
	
	private class ModificationRecord implements Serializable
	{
		private static final long serialVersionUID = 1L;

		DataNormalizationRule rule;
		ModificationType type;
		String subject;
		String predicate;
		String object;
		
		public ModificationRecord(DataNormalizationRule rule, ModificationType type, 
				String subject, String predicate, String object) {
			this.rule = rule;
			this.type = type;
			this.subject = subject;
			this.predicate = predicate;
			this.object = object;
		}

		public DataNormalizationRule getRule() {
			return rule;
		}

		public ModificationType getType() {
			return type;
		}

		public String getSubject() {
			return subject;
		}

		public String getPredicate() {
			return predicate;
		}

		public String getObject() {
			return object;
		}
	}
	
	private enum ModificationType {
		DELETE(0),
		INSERT(1);
		
		Integer order;
		
		ModificationType(Integer order) {
			this.order = order;
		}
	}
	
	private List<ModificationRecord> flatten(GraphModification graphMod)
	{
		List<ModificationRecord> result = new ArrayList<ModificationRecord>();
		
		Iterator<DataNormalizationRule> it = graphMod.getRuleIterator();
		while (it.hasNext())
		{
			DataNormalizationRule rule = it.next();
			RuleModification ruleMod = graphMod.getModificationsByRule(rule);
			
			for (TripleModification tripleMod: ruleMod.getInsertions())
			{
				result.add(new ModificationRecord(
					rule,
					ModificationType.INSERT,
					tripleMod.getSubject(),
					tripleMod.getPredicate(),
					tripleMod.getObject()
				));
			}
			for (TripleModification tripleMod: ruleMod.getDeletions())
			{
				result.add(new ModificationRecord(
					rule,
					ModificationType.DELETE,
					tripleMod.getSubject(),
					tripleMod.getPredicate(),
					tripleMod.getObject()
				));
			}
		}
		
		return result;
	}
}
