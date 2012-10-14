package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import java.util.regex.Pattern;

import cz.cuni.mff.odcleanstore.shared.Utils;

public class DNConcatenateTemplateInstanceCompiler 
	extends DNTemplateInstanceCompiler<DNConcatenateTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	public CompiledDNRule compile(DNConcatenateTemplateInstance instance)
	{
		// 1. Create rule.
		//
		String description = String.format
		(
			"Raw form of a cancatenate rule template instance. " +
			"Property: %s; Delimiter: '%s';", 
			instance.getPropertyName(),
			instance.getDelimiter()
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
		
		String delimiter = instance.getDelimiter();
		
		delimiter = charsToBeRemoved.matcher(delimiter).replaceAll("");
		delimiter = charsToBeEscaped.matcher(delimiter).replaceAll("\\\\$1");
		
		String modification = String.format
		(
			"DELETE {?s ?p ?o} INSERT {?s ?p ?c} WHERE { GRAPH $$graph$$ {SELECT ?s ?p (sql:group_concat(str(?o), '%s')) AS ?c WHERE {?s ?p ?o} GROUP BY ?s ?p HAVING COUNT(?o) > 1} {?s ?p ?o} FILTER (?p = %s)}",
			delimiter,
			property
		);

		String compDescription = "Concatenate all objects of the property.";
		
		CompiledDNRuleComponent component = new CompiledDNRuleComponent
		(
			CompiledDNRuleComponent.TypeLabel.MODIFY,
			modification,
			compDescription
		);

		// 3. Register components with rule.
		//
		rule.addComponent(component);
		
		return rule;
	}
}
