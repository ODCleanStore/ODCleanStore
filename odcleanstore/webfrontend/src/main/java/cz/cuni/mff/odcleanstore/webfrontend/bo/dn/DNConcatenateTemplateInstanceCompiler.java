package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.shared.Utils;

/**
 * A compiler to translate concatenate template instances into raw rules.
 * 
 * @author Jakub Daniel
 *
 */
public class DNConcatenateTemplateInstanceCompiler 
	extends DNTemplateInstanceCompiler<DNConcatenateTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param instance
	 */
	@Override
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
		String property = Utils.escapeSPARQLLiteral(instance.getPropertyName());
		
		if (!Utils.isPrefixedName(property)) {
			property = "<" + property + ">";
		}
		
		String delimiter = instance.getDelimiter();

		if (delimiter == null) delimiter = "";
		
		delimiter = Utils.escapeSPARQLLiteral(delimiter);
		
		String modification = String.format
		(
			"DELETE {?s ?p ?o} INSERT {?s ?p ?c} WHERE { {SELECT ?s ?p (sql:group_concat(str(?o), '%s')) AS ?c WHERE {?s ?p ?o} GROUP BY ?s ?p HAVING COUNT(?o) > 1} {?s ?p ?o} FILTER (?p = %s)}",
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
