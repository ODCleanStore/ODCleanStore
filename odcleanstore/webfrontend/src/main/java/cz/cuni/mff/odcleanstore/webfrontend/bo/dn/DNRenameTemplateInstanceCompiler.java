package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

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
		String label;

		if (instance.getLabel() == null)
		{
			label = instance.getSourcePropertyName() + "-rename-rule";
		}
		else
		{
			label = instance.getLabel();
		}

		String description;

		if (instance.getDescription() == null)
		{
			description = String.format
			(
				"Raw form of a rename rule template instance. " +
				"Source property: %s; Target property: %s;",
				instance.getSourcePropertyName(),
				instance.getTargetPropertyName()
			);
		}
		else
		{
			description = instance.getDescription();
		}
		
		CompiledDNRule rule = new CompiledDNRule(instance.getGroupId(), label, description);

		// 2. Create components.
		//
		String sourceProperty = ODCSUtils.escapeSPARQLLiteral(instance.getSourcePropertyName());
		
		if (!ODCSUtils.isPrefixedName(sourceProperty)) {
			sourceProperty = "<" + sourceProperty + ">";
		}
		
		String targetProperty = ODCSUtils.escapeSPARQLLiteral(instance.getTargetPropertyName());
		
		if (!ODCSUtils.isPrefixedName(targetProperty)) {
			targetProperty = "<" + targetProperty + ">";
		}

		String modification = String.format
		(
			"DELETE {?s %s ?o} INSERT {?s %s ?o} WHERE { ?s %s ?o}",
			sourceProperty,
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
