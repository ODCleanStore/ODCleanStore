package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

import java.net.URI;

public class Publisher extends BusinessObject 
{
	private static final long serialVersionUID = 1L;
	
	private URI uri;

	public Publisher(Long id, URI uri) 
	{
		this.id = id;
		this.uri = uri;
	}
	
	public Publisher()
	{
	}

	public URI getUri() 
	{
		return uri;
	}
}
