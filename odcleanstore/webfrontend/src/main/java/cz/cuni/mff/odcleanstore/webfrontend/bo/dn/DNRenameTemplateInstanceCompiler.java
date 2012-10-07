package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import java.util.regex.Pattern;

import cz.cuni.mff.odcleanstore.shared.Utils;

public class DNRenameTemplateInstanceCompiler 
{
	public static CompiledDNRule compile(DNRenameTemplateInstance instance)
	{
		// 1. Create rule.
		//
		String description = String.format
		(
			"Raw form of a rename rule template instance. " +
			"Source property: %s; Target property: %s;", 
			instance.getSourcePropertyName(), 
			instance.getTargetPropertyName()
		);
		
		CompiledDNRule rule = new CompiledDNRule(instance.getGroupId(), description);

		// 2. Create components.
		//
		Pattern charsToBeRemoved = Pattern.compile("[\\x00-\\x09\\x0E-\\x1F]");
		Pattern charsToBeEscaped = Pattern.compile("([\"'`\\\\])");

		String sourceProperty = instance.getSourcePropertyName();
		
		if (!Utils.isPrefixedName(sourceProperty)) {
			sourceProperty = "<" + sourceProperty + ">";
		}
		
		sourceProperty = charsToBeRemoved.matcher(sourceProperty).replaceAll("");
		sourceProperty = charsToBeEscaped.matcher(sourceProperty).replaceAll("\\\\$1");
		
		String targetProperty = instance.getTargetPropertyName();
		
		if (!Utils.isPrefixedName(targetProperty)) {
			targetProperty = "<" + targetProperty + ">";
		}

		targetProperty = charsToBeRemoved.matcher(targetProperty).replaceAll("");
		targetProperty = charsToBeEscaped.matcher(targetProperty).replaceAll("\\\\$1");

		String modification = String.format
		(
			"DELETE {?s ?p ?o} INSERT {?s %s ?o} WHERE { ?s ?p ?o FILTER (?p = %s)}", 
			targetProperty, 
			sourceProperty
		);

		CompiledDNRuleComponent component = new CompiledDNRuleComponent
		(
			CompiledDNRuleComponent.TypeLabel.MODIFY,
			modification,
			"Remove old properties and add new ones in the same step."
		);

		// 3. Register components with rule.
		//
		rule.addComponent(component);
		
		return rule;
	}
}
