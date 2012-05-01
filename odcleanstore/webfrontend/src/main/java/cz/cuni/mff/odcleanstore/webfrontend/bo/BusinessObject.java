package cz.cuni.mff.odcleanstore.webfrontend.bo;

import java.io.Serializable;

/**
 * The base class for all business objects.
 * 
 * @author Dušan Rychnovský
 *
 */
public abstract class BusinessObject implements Serializable 
{
	protected Long id;

	/**
	 * 
	 * @return
	 */
	public Long getId() 
	{
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id) 
	{
		this.id = id;
	}
}
