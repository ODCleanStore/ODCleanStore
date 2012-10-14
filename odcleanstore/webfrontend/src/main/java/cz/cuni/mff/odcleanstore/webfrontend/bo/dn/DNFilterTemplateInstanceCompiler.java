package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import java.util.regex.Pattern;

import cz.cuni.mff.odcleanstore.shared.Utils;

public class DNFilterTemplateInstanceCompiler 
	extends DNTemplateInstanceCompiler<DNFilterTemplateInstance>
{	
	private static final long serialVersionUID = 1L;

	public CompiledDNRule compile(DNFilterTemplateInstance instance)
	{
		// 1. Create rule.
		//
		String description = String.format
		(
			"Raw form of a filter rule template instance. " +
			"Property: %s; Pattern: %s; Keep: %s;", 
			instance.getPropertyName(), 
			instance.getPattern(),
			instance.getKeep().toString()
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
		
		String modification = String.format
		(
			"{?s ?p ?o} WHERE { ?s ?p ?o FILTER (?p = %s AND %sfn:matches(str(?o), '%s'))}", 
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
