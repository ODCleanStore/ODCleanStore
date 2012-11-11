package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.shared.Utils;

/**
 * A compiler to translate replace template instances into raw rules.
 * 
 * @author Dušan Rychnovský
 *
 */
public class DNReplaceTemplateInstanceCompiler 
	extends DNTemplateInstanceCompiler<DNReplaceTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param instance
	 */
	@Override
	public CompiledDNRule compile(DNReplaceTemplateInstance instance)
	{
		// 1. Create rule.
		//
		String description = String.format
		(
			"Raw form of a replace rule template instance. " +
			"Property: %s; Pattern: %s; Replacement: %s;", 
			instance.getPropertyName(), 
			instance.getPattern(), 
			instance.getReplacement()
		);
		
		CompiledDNRule rule = new CompiledDNRule(instance.getGroupId(), instance.getPropertyName() + "-replace-rule", description);
		
		// 2. Create components.
		//
		String property = Utils.escapeSPARQLLiteral(instance.getPropertyName());
		
		if (!Utils.isPrefixedName(property)) {
			property = "<" + property + ">";
		}
		
		String pattern = Utils.escapeSPARQLLiteral(instance.getPattern());

		String replacement = Utils.escapeSPARQLLiteral(instance.getReplacement());

		String modification = String.format
		(
			"DELETE {?s %s ?o} INSERT {?s %s ?x} " +
			"WHERE { {SELECT ?s ?o (fn:replace(str(?o), '%s', '%s')) AS ?x WHERE {{?s %s ?o}}} FILTER (BOUND(?x))}",
			property,
			property,
			pattern, 
			replacement, 
			property
		);
		
		CompiledDNRuleComponent component = new CompiledDNRuleComponent
		(
			CompiledDNRuleComponent.TypeLabel.MODIFY,
			modification, 
			"Remove old values and add transformed values in the same step."
		);

		// 3. Register components with rule.
		//
		rule.addComponent(component);
		
		return rule;
	}
}
