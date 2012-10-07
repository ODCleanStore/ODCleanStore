package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent a component of a compiled DN rule.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class CompiledDNRuleComponent extends EntityWithSurrogateKey 
{
	public enum TypeLabel { INSERT, DELETE, MODIFY };
	
	private static final long serialVersionUID = 1L;
	
	private TypeLabel typeLabel;
	private String modification;
	private String description;
	
	/**
	 * 
	 * @param typeLabel
	 * @param modification
	 * @param description
	 */
	public CompiledDNRuleComponent(TypeLabel typeLabel, String modification, String description) 
	{
		this.typeLabel = typeLabel;
		this.modification = modification;
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public TypeLabel getTypeLabel() 
	{
		return typeLabel;
	}

	/**
	 * 
	 * @return
	 */
	public String getModification() 
	{
		return modification;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() 
	{
		return description;
	}
}
