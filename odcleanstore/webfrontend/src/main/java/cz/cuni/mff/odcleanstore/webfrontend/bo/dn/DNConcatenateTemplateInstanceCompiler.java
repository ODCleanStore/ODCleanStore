package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

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
	 * Creates a rule that can be used with DataNormalizer and performs action
	 * expected from the instance of the concatenation template
	 * 
	 * @param instance filled in template for a concatenation rule
	 */
	@Override
	public CompiledDNRule compile(DNConcatenateTemplateInstance instance)
	{
		// 1. Create rule.
		//
		String delimiter = instance.getDelimiter();

		if (delimiter == null) delimiter = "";

		String description = String.format
		(
			"Raw form of a cancatenate rule template instance. " +
			"Property: %s; Delimiter: '%s';", 
			instance.getPropertyName(),
			delimiter
		);
		
		CompiledDNRule rule = new CompiledDNRule(instance.getGroupId(), instance.getPropertyName() + "-concatenate-rule", description);

		// 2. Create components.
		//
		String property = ODCSUtils.escapeSPARQLLiteral(instance.getPropertyName());
		
		if (!ODCSUtils.isPrefixedName(property)) {
			property = "<" + property + ">";
		}
		
		delimiter = ODCSUtils.escapeSPARQLLiteral(delimiter);
		
		String modification = String.format
		(
			"DELETE {?s %s ?o} INSERT {?s %s ?c} WHERE { {SELECT ?s ?p (sql:group_concat(str(?o), '%s')) AS ?c WHERE {?s ?p ?o} GROUP BY ?s ?p HAVING COUNT(?o) > 1} {?s ?p ?o} FILTER (BOUND(?c))}",
			property,
			property,
			delimiter
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
