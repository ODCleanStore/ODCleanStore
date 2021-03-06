package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.core.ODCSUtils;

/**
 * A compiler to translate filter template instances into raw rules.
 *
 * @author Dušan Rychnovský
 *
 */
public class DNFilterTemplateInstanceCompiler
	extends DNTemplateInstanceCompiler<DNFilterTemplateInstance>
{	
	private static final long serialVersionUID = 1L;

	/**
	 *
	 * @param instance
	 */
	@Override
	public CompiledDNRule compile(DNFilterTemplateInstance instance)
	{
		// 1. Create rule.
		//
		String label;

		if (instance.getLabel() == null)
		{
			label = instance.getPropertyName() + "-filter-rule";
		}
		else
		{
			label = instance.getLabel();
		}

		String description;

		if (instance.getDescription() == null)
		{
			description = String.format
			(
				"Raw form of a filter rule template instance. " +
				"Property: %s; Pattern: %s; Keep: %s;",
				instance.getPropertyName(),
				instance.getPattern(),
				instance.getKeep().toString()
			);
		}
		else
		{
			description = instance.getDescription();
		}
		
		CompiledDNRule rule = new CompiledDNRule(instance.getGroupId(), label, description);

		// 2. Create components.
		//
		String property = instance.getPropertyName();
		
		if (!ODCSUtils.isPrefixedName(property)) {
			property = "<" + property + ">";
		}
		
		property = ODCSUtils.escapeSPARQLLiteral(property);
		
		String pattern = ODCSUtils.escapeSPARQLLiteral(instance.getPattern());

		String modification = String.format
		(
			"{?s %s ?o} WHERE { ?s %s ?o FILTER (%sfn:matches(str(?o), '%s'))}",
			property,
			property,
			instance.getKeep() ? "!" : "",
			pattern
		);

		String compDescription = String.format
		(
			"Filter out %smatching values of the property.",
			instance.getKeep() ? "non-" : ""
		);
		
		CompiledDNRuleComponent component = new CompiledDNRuleComponent
		(
			CompiledDNRuleComponent.TypeLabel.DELETE,
			modification,
			compDescription
		);

		// 3. Register components with rule.
		//
		rule.addComponent(component);
		
		return rule;
	}
}
