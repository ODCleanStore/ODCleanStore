package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * The BO to represent a pipeline.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class Pipeline extends EntityWithSurrogateKey implements AuthoredEntity
{
	private static final long serialVersionUID = 1L;

	private String label;
	private String description;
	private Boolean isDefault;
	private Boolean isLocked;
	private List<TransformerInstance> transformers;
	private Integer authorId;
	private String authorName;
	
	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param isDefault
	 * @param isLocked
	 * @param authorId
	 * @param authorName
	 */
	public Pipeline(Integer id, String label, String description, Boolean isDefault, Boolean isLocked, Integer authorId, String authorName) 
	{
		super(id);
		
		this.transformers = new LinkedList<TransformerInstance>();
		
		this.label = label;
		this.description = description;
		this.isDefault = isDefault;
		this.isLocked = isLocked;
		this.authorId = authorId;
		this.setAuthorName(authorName);
	}
	
	/**
	 * 
	 */
	public Pipeline()
	{
		transformers = new LinkedList<TransformerInstance>();
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
	 * @param label
	 */
	public void setLabel(String label) 
	{
		this.label = label;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() 
	{
		return description;
	}
	
	public void setDescription(String description) 
	{
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public Boolean isDefault() 
	{
		return isDefault;
	}
	
	/**
	 * 
	 * @param isDefault
	 */
	public void setDefault(Boolean isDefault)
	{
		this.isDefault = isDefault;
	}
	
	/**
	 * 
	 * @param authorId
	 */
	public void setAuthorId(Integer authorId) 
	{
		this.authorId = authorId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Integer getAuthorId() 
	{
		return authorId;
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean isLocked() 
	{
		return isLocked;
	}
	
	/**
	 * 
	 * @param isLocked
	 */
	public void setLocked(Boolean isLocked)
	{
		this.isLocked = isLocked;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<TransformerInstance> getTransformers()
	{
		return transformers;
	}
	
	/**
	 * 
	 * @param transformers
	 */
	public void setTransformers(List<TransformerInstance> transformers)
	{
		this.transformers = transformers;
	}

	/**
	 * 
	 * @return
	 */
	public String getAuthorName()
	{
		return authorName;
	}

	/**
	 * 
	 * @param authorName
	 */
	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}
}
