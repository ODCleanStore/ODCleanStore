package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.shared.Utils;

/**
 * A compiler to translate rename template instances into raw rules.
 * 
 * @author Dušan Rychnovský
 *
 */
public class DNRenameTemplateInstanceCompiler 
	extends DNTemplateInstanceCompiler<DNRenameTemplateInstance>
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param instance
	 */
	@Override
	public CompiledDNRule compile(DNRenameTemplateInstance instance)
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
		String sourceProperty = Utils.escapeSPARQLLiteral(instance.getSourcePropertyName());
		
		if (!Utils.isPrefixedName(sourceProperty)) {
			sourceProperty = "<" + sourceProperty + ">";
		}
		
		String targetProperty = Utils.escapeSPARQLLiteral(instance.getTargetPropertyName());
		
		if (!Utils.isPrefixedName(targetProperty)) {
			targetProperty = "<" + targetProperty + ">";
		}

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
