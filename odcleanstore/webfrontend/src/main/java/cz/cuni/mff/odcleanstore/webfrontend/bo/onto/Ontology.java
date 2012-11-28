package cz.cuni.mff.odcleanstore.webfrontend.bo.onto;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;

/**
 * Business entity representing an ontology.
 * 
 * @author Tomas Soukup
 */
public class Ontology extends AuthoredEntity
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String description;
	private String graphName;
	private String definition;
	private Integer authorId;
	private String authorName;
	private Integer qaRulesGroup = null;
	private Integer dnRulesGroup = null;

	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param graphName
	 * @param authorId
	 * @param authorName
	 * @param definition
	 */
	public Ontology(Integer id, String label, String description, String graphName, Integer authorId,
			String authorName, String definition) 
	{
		super(id);
		this.label = label;
		this.description = description;
		this.graphName = graphName;
		this.definition = definition;
		this.authorId = authorId;
		this.setAuthorName(authorName);
	}
	
	public Ontology()
	{
		super();
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
	
	/**
	 * @return
	 */
	public String getGraphName() {
		return graphName;
	}

	/**
	 * @param graphName
	 */
	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	/**
	 * @return
	 */
	public String getDefinition() {
		return definition;
	}

	/**
	 * @param definition
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/* (non-Javadoc)
	 * @see cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity#getAuthorId()
	 */
	public Integer getAuthorId() 
	{
		return authorId;
	}
	
	/**
	 * @return
	 */
	public Integer getQaRulesGroup() {
		return qaRulesGroup;
	}
	
	/**
	 * @return
	 */
	public Integer getDnRulesGroup() {
		return dnRulesGroup;
	}
	
	/**
	 * @param authorId
	 */
	public void setAuthorId(Integer authorId) 
	{
		this.authorId = authorId;
	}

	/**
	 * @return
	 */
	public String getAuthorName()
	{
		return authorName;
	}

	/**
	 * @param authorName
	 */
	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}
	
	/**
	 * @param qaRulesGroup
	 */
	public void setQaRulesGroup(Integer qaRulesGroup) {
		this.qaRulesGroup = qaRulesGroup;
	}

	/**
	 * @param dnRulesGroup
	 */
	public void setDnRulesGroup(Integer dnRulesGroup) {
		this.dnRulesGroup = dnRulesGroup;
	}
}
