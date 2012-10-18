package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import java.util.regex.Pattern;

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
		
		CompiledDNRule rule = new CompiledDNRule(instance.getGroupId(), description);
		
		// 2. Create components.
		//
		Pattern charsToBeRemoved = Pattern.compile("[\\x00-\\x09\\x0E-\\x1F]");
		Pattern charsToBeEscaped = Pattern.compile("([\"'`\\\\])");

		String property = instance.getPropertyName();
		
		if (!Utils.isPrefixedName(property)) {
			property = "<" + property + ">";
		}
		
		property = charsToBeRemoved.matcher(property).replaceAll("");
		property = charsToBeEscaped.matcher(property).replaceAll("\\\\$1");
		
		String pattern = instance.getPattern();

		pattern = charsToBeRemoved.matcher(pattern).replaceAll("");
		pattern = charsToBeEscaped.matcher(pattern).replaceAll("\\\\$1");
		
		String replacement = instance.getReplacement();

		replacement = charsToBeRemoved.matcher(replacement).replaceAll("");
		replacement = charsToBeEscaped.matcher(replacement).replaceAll("\\\\$1");

		String modification = String.format
		(
			"DELETE {?s ?p ?o} INSERT {?s ?p ?x} " +
			"WHERE { {SELECT ?s ?p ?o (fn:replace(str(?o), '%s', '%s')) AS ?x WHERE {{?s ?p ?o} FILTER (?p = %s)}}}", 
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
