package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;

/**
 * The BO to represent a group of DN rules.
 * 
 * @author Dusan
 *
 */
public class DNRulesGroup extends RulesGroupEntity 
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 */
	public DNRulesGroup(Integer id, String label, String description) 
	{
		super(id);
		
		this.label = label;
		this.description = description;
	}

	/**
	 * 
	 */
	public DNRulesGroup() 
	{
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() 
	{
		return label;
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
