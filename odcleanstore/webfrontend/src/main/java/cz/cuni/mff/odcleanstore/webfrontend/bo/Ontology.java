package cz.cuni.mff.odcleanstore.webfrontend.bo;

public class Ontology 
{
	private String title;
	private String definition;
	
	/**
	 * 
	 * @param title
	 * @param definition
	 */
	public Ontology(String title, String definition) 
	{
		this.title = title;
		this.definition = definition;
	}
	
	/**
	 * 
	 */
	public Ontology()
	{		
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() 
	{
		return title;
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) 
	{
		this.title = title;
	}

	/**
	 * 
	 * @return
	 */
	public String getDefinition() 
	{
		return definition;
	}

	/**
	 * 
	 * @param definition
	 */
	public void setDefinition(String definition) 
	{
		this.definition = definition;
	}
}
