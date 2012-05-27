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
	
	public void setUri(URI uri)
	{
		this.uri = uri;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Publisher))
			return false;
		
		Publisher otherPublisher = (Publisher) other;

		return this.uri == otherPublisher.uri;
	}
}
