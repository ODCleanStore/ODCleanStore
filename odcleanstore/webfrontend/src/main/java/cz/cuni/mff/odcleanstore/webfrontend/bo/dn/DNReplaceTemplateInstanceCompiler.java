package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

public class DNReplaceTemplateInstanceCompiler 
{
	public static CompiledDNRule compile(DNReplaceTemplateInstance instance)
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
		String modification = String.format
		(
			"DELETE {?s ?p ?o} INSERT {?s ?p ?x} " +
			"WHERE { {SELECT ?s ?p ?o (fn:replace(str(?o), '%s', '%s')) AS ?x WHERE {{?s ?p ?o} FILTER (?p = %s)}}}", 
			instance.getPattern(), 
			instance.getReplacement(), 
			instance.getPropertyName()
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
