package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

import java.net.URI;

public class Publisher extends BusinessObject 
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private String uri;

	/**
	 * 
	 * @param id
	 * @param label
	 * @param uri
	 */
	public Publisher(Long id, String label, String uri) 
	{
		this.id = id;
		this.label = label;
		this.uri = uri;
	}
	
	public Publisher()
	{
	}

	public String getLabel()
	{
		return label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public String getUri() 
	{
		return uri;
	}
	
	public void setUri(String uri)
	{
		this.uri = uri;
	}
}
