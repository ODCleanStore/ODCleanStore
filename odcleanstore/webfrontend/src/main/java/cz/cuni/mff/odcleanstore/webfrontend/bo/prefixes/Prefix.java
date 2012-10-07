package cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

/**
 * The BO representing an RDF prefix.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class Prefix extends BusinessEntity
{
	private static final long serialVersionUID = 1L;

	private String prefix;
	private String url;
	
	/**
	 * 
	 * @param prefix
	 * @param url
	 */
	public Prefix(String prefix, String url) 
	{
		this.prefix = prefix;
		this.url = url;
	}

	/**
	 * 
	 */
	public Prefix() 
	{
	}

	/**
	 * 
	 * @return
	 */
	public String getPrefix() 
	{
		return prefix;
	}

	/**
	 * 
	 * @return
	 */
	public String getUrl() 
	{
		return url;
	}
}
