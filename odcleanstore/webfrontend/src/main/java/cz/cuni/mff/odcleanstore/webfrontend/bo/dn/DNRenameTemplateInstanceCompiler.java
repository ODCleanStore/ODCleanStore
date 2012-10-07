package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

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
		String modification = String.format
		(
			"DELETE {?s ?p ?o} INSERT {?s %s ?o} WHERE { ?s ?p ?o FILTER (?p = %s)}", 
			instance.getTargetPropertyName(), 
			instance.getSourcePropertyName()
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
