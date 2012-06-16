package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class Graph extends BusinessObject
{
	private static final long serialVersionUID = 1L;
	
	private String uri;
	
	// TODO: to be implemented
	
	public Graph(String uri)
	{
		this.uri = uri;
	}

	public String getUri() 
	{
		return uri;
	}
}
