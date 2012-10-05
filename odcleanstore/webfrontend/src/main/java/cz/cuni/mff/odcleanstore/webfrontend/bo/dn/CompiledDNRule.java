package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import java.util.LinkedList;
import java.util.List;

/**
 * The BO to represent a compiled DN rule. Contains a list of
 * dependent components to ease manipulation.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class CompiledDNRule extends DNRule
{
	private static final long serialVersionUID = 1L;

	private List<CompiledDNRuleComponent> components;
	
	/**
	 * 
	 * @param id
	 * @param groupId
	 * @param description
	 */
	public CompiledDNRule(Integer groupId, String description) 
	{
		super(groupId, description);

		this.components = new LinkedList<CompiledDNRuleComponent>();
	}

	/**
	 * 
	 * @return
	 */
	public List<CompiledDNRuleComponent> getComponents() 
	{
		return components;
	}
	
	/**
	 * 
	 * @param component
	 */
	public void addComponent(CompiledDNRuleComponent component)
	{
		this.components.add(component);
	}
}
